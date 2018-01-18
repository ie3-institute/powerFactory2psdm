'''
@author: Kittl
'''
from exportHelpers import correctPrefix
def exportPowerPlants(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "PowerPlant"
    elif exportProfile is 3:
        cmpStr = ""
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for power plants" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 7 indicates the powerPlant-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('##############################################')
    dpf.PrintPlain('# Starting to export renewable power plants. #')
    dpf.PrintPlain('##############################################')

    expMat = list()
    expMat.append(colHead)

    pps = dpf.GetCalcRelevantObjects('*.ElmSym')
    for pp in pps:
        if exportProfile is 2:
            expMat.append([
                pp.loc_name,  # id
                pp.bus1.cterm.loc_name,  # node
                pp.typ_id.loc_name if pp.typ_id is not None else "",  # type
                pp.pgini*correctPrefix(dpf, "k", "PQS"),  # pPP
                pp.qgini*correctPrefix(dpf, "k", "PQS"),  # qPP
                pp.av_mode == 'constv', # vControl
                pp.typ_id.sgn*correctPrefix(dpf, "k", "PQS") if pp.typ_id is not None else 0,  # sR
                "",  # pMin
                "",  # pMax
                "",  # qMin
                "",  # qMax
                pp.cpArea.loc_name if pp.cpArea is not None else "",  # subnet
                pp.cpZone.loc_name if pp.cpZone is not None else ""  # voltLvl
            ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)