'''
@author: Kittl
'''
from exportHelpers import correctPrefix
import sys
def exportRES(dpf, comLdf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "RES"
    elif exportProfile is 3:
        cmpStr = "StatGen"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for renewable energy sources" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 6 indicates the RES-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('################################################')
    dpf.PrintPlain('# Starting to export renewable energy sources. #')
    dpf.PrintPlain('################################################')

    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['StatGendaten']
        ] + expMat

    reses = dpf.GetCalcRelevantObjects('*.ElmGenstat')
    if exportProfile == 3:
        reses += dpf.GetCalcRelevantObjects('*.ElmPvsys')
    for res in reses:
        if res.outserv == 1:
            break
        
        if exportProfile is 2:
            expMat.append([
                res.loc_name,  # id
                res.bus1.cterm.loc_name,  # node
                res.cCategory,  # type
                "",  # pvType
                "",  # wecType
                "",  # bmType#
                res.pgini*correctPrefix(dpf, "k", "PQS"),  # pRES
                res.qgini*correctPrefix(dpf, "k", "PQS"),  # qRES
                res.sgn*correctPrefix(dpf, "k", "PQS"),  # sR
                res.cpArea.loc_name if res.cpArea is not None else "",  # subnet
                res.cpZone.loc_name if res.cpZone is not None else ""  # voltLvl
            ])
        elif exportProfile is 3:
            # genCat = ['Wasser', 'Wind', 'Biogas', 'Solar', 'Fotovoltaik', 'Erneuerbare Erzeugung', 'Statischer Generator']
            # if res.cCategory in genCat:
            
            bus = res.bus1.cterm
            #  Check if one of the nodes is an internal node of a substation
            if bus.IsInternalNodeInStation() == 1:
                mainBuses = bus.GetConnectedMainBuses()
                if len(mainBuses) == 0:
                    dpf.PrintWarn('Distributed generator "'+res.loc_name+'" is connected to an internal node of a substation without being connected main node. Most probably this is not really connected. Check it!')
                    continue
                elif len(mainBuses) > 1:
                    dpf.PrintError('Node '+res.loc_name+' has more than one main bus. Abort the script!')
                    sys.exit()
                else:
                    bus = mainBuses[0]
            if res.GetClassName() == 'ElmPvsys':
                expMat.append([
                    res.loc_name,   # ID
                    bus.loc_name,    # Knoten
                    res.pgini / res.cosgini * res.ngnum * res.scale0 * correctPrefix(dpf, 'M', 'k'), # Leistung
                    res.cosgini if res.pf_recap == 0 else -res.cosgini, # cosphi
                    'Fotovoltaik',
                    res.cpArea.loc_name if res.cpArea is not None else "",  # subnet
                    res.cpZone.loc_name if res.cpZone is not None else ""  # voltLvl
                ])
            else:
                expMat.append([
                    res.loc_name,   # ID
                    bus.loc_name,    # Knoten
                    res.sgini_a, # Leistung
                    res.cosgini if res.pf_recap == 0 else -res.cosgini, # cosphi
                    res.cCategory,
                    res.cpArea.loc_name if res.cpArea is not None else "",  # subnet
                    res.cpZone.loc_name if res.cpZone is not None else ""  # voltLvl
                ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)