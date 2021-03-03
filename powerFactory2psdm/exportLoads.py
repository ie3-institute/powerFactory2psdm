'''
@author: Kittl
'''
from exportHelpers import correctPrefix
from builtins import str
from math import sqrt, cos, acos, tan, atan, inf
import sys

def exportLoads(dpf, comLdf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Load"
    elif exportProfile is 3:
        cmpStr = "Lasten"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for loads" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 5 indicates the loads-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#############################')
    dpf.PrintPlain('# Starting to export loads. #')
    dpf.PrintPlain('#############################')
    
    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['Lastdaten']
        ] + expMat

    scaleStorHeating = comLdf.scPnight / 100    # Scaling factor for storage heaters comes as percentage
    loads = dpf.GetCalcRelevantObjects('*.ElmLod')
    loads += dpf.GetCalcRelevantObjects('*.ElmLodlv')
    loads += dpf.GetCalcRelevantObjects('*.ElmLodmv')
    for load in loads:
        if load.GetClassName() == "ElmLod":
            if exportProfile is 2:
                expMat.append([
                    load.loc_name,  # id
                    load.bus1.cterm.loc_name,  # node
                    load.typ_id.loc_name if load.typ_id is not None else "",  # type
                    load.plini_a*correctPrefix(dpf, "M", "PQS"),  # pload
                    load.qlini_a*correctPrefix(dpf, "M", "PQS"),  # qLoad
                    load.cpArea.loc_name if load.cpArea is not None else "",  # subnet
                    load.cpZone.loc_name if load.cpZone is not None else ""  # voltLvl
                ])
            elif exportProfile is 3:
                bus = load.bus1.cterm
                #  Check if one of the nodes is an internal node of a substation
                if bus.IsInternalNodeInStation() == 1:
                    mainBuses = bus.GetConnectedMainBuses()
                    if len(mainBuses) == 0:
                        dpf.PrintWarn('Load "'+load.loc_name+'" is connected to an internal node of a substation without being connected main node. Most probably this is not really connected. Check it!')
                        continue
                    elif len(mainBuses) > 1:
                        dpf.PrintError('Node '+load.loc_name+' has more than one main bus. Abort the script!')
                        sys.exit()
                    else:
                        bus = mainBuses[0]
                    
                expMat.append([
                    load.loc_name,  # ID
                    bus.loc_name,  # Knoten
                    load.slini_a*correctPrefix(dpf, "k", "PQS"),  # Anschlusswert (Scheinleistung)
                    load.coslini,  # cosphi
                    0,  # Jahreswert
                    0,  # DSM
                    "",   # Szenario
                    load.cpArea.loc_name if load.cpArea is not None else "",  # Teilnetz
                    load.cpZone.loc_name if load.cpZone is not None else ""  # Spgsebene
                ])
            else:
                dpf.PrintError("This export profile isn't implemented yet.")
                exit(1)
        elif load.GetClassName() == "ElmLodlv":
            if exportProfile is 2:
                p = load.plini_a * correctPrefix(dpf, "k", "PQS");
                
                # Consider storage heating systems
                # They are modeled with a power factor of 1
                if not load.pnight == 0:
                    # Get the correct scaling factor, which is composed of an individual scaling factor, a scaling factor of the load flow calculation object and a scaling factor of the zone
                    loadScale = load.scale0 * comLdf.scLoadFac/100 * load.cpZone.curscale * scaleStorHeating
                    p += load.pnight * correctPrefix(dpf, "k", "k") * loadScale   # Storage Heater
                
                # Distinguish between inductive and capacitive power factor
                # The capacitive factor is defined as negative
                if load.pf_recap == 0: # power factor is inductive
                    cosphi = load.coslini;
                else:   # power factor is capacitive
                    cosphi = -load.coslini;
                
                expMat.append([
                    load.loc_name,  # id
                    load.bus1.cterm.loc_name,  # node
                    load.typ_id.loc_name if load.typ_id is not None else "",  # type
                    p,  # pload
                    cosphi,  # cosphi
                    load.cpArea.loc_name if load.cpArea is not None else "",  # subnet
                    load.cpZone.loc_name if load.cpZone is not None else ""  # voltLvl
                ])
            elif exportProfile is 3:
                p = load.plini_a * correctPrefix(dpf, "k", "PQS");
                
                # Distinguish between inductive and capacitive power factor
                # The capacitive factor is defined as negative
                if load.pf_recap == 0: # power factor is inductive
                    signQL = +1;
                else:   # power factor is capacitive
                    signQL = -1;
                
                q = signQL * p * tan( acos( load.coslini ) )
                
                # Consider storage heating systems
                # They are modeled with a power factor of 1
                if not load.pnight == 0:
                    # Get the correct scaling factor, which is composed of an individual scaling factor, a scaling factor of the load flow calculation object and a scaling factor of the zone
                    # For storage heating the individual scaling factor and the one in the calculation object are not considered
                    loadScale = load.cpZone.curscale * scaleStorHeating
                    p += load.pnight * correctPrefix(dpf, "k", "k") * loadScale   # Storage Heater
                
                # Consider distinct households
                if not load.cNrCust == 0:
                    # Get the correct scaling factor, which is composed of an individual scaling factor, a scaling factor of the load flow calculation object and a scaling factor of the zone
                    # For residential load the individual scaling factor is not considered
                    loadScale = comLdf.scLoadFac/100 * load.cpZone.curscale
                    p += load.cNrCust * load.cPrCust * loadScale * correctPrefix(dpf, "k", "k")
                    q += load.cNrCust * load.cPrCust * loadScale * tan( acos( load.ccosphi ) ) * correctPrefix(dpf, "k", "k")
                
                # Recalculate the resulting apparent power and the power factor
                s = sqrt( p**2 + q**2 )
                if not p == 0:
                    cosphi = cos( atan ( q / p ) )
                else:
                    cosphi = 0
                    dpf.PrintWarn('Low voltage load model '+load.loc_name+' has no active power component!')
                    
                bus = load.bus1.cterm
                #  Check if one of the nodes is an internal node of a substation
                if bus.IsInternalNodeInStation() == 1:
                    mainBuses = bus.GetConnectedMainBuses()
                    if len(mainBuses) == 0:
                        dpf.PrintWarn('Load "'+load.loc_name+'" is connected to an internal node of a substation without being connected main node. Most probably this is not really connected. Check it!')
                        continue
                    elif len(mainBuses) > 1:
                        dpf.PrintError('Node '+load.loc_name+' has more than one main bus. Abort the script!')
                        sys.exit()
                    else:
                        bus = mainBuses[0]
                
                expMat.append([
                    load.loc_name,  # ID
                    bus.loc_name,  # Knoten
                    s,  # Anschlusswert (Scheinleistung)
                    cosphi,  # cosphi
                    load.elini*correctPrefix(dpf, "k", "k"),    # Jahreswert
                    0,  # DSM
                    "",   # Szenario
                    load.cpArea.loc_name if load.cpArea is not None else "",  # subnet
                    load.cpZone.loc_name if load.cpZone is not None else ""  # voltLvl
                ])
            else:
                dpf.PrintError("This export profile isn't implemented yet.")
                exit(1)
        elif load.GetClassName() == "ElmLodmv":
            if exportProfile is 3:
                # Get the correct scaling factor, which is composed of an individual scaling factor, a scaling factor of the load flow calculation object and a scaling factor of the zone
                loadScale = load.scale0 * comLdf.scLoadFac/100 * load.cpZone.curscale                
                p = load.plini_a * correctPrefix(dpf, "k", "PQS");
                
                # Distinguish between inductive and capacitive power factor
                # The capacitive factor is defined as negative
                if load.pf_recap == 0: # power factor is inductive
                    signQL = +1;
                else:   # power factor is capacitive
                    signQL = -1;
                
                q = signQL * p * tan( acos( load.coslini ) )
                
                if not load.sgini == 0:
                    pgen = load.pgini_a * correctPrefix(dpf, "k", "M")
                        
                    # Distinguish between inductive and capacitive power factor
                    # The inductive factor is defined as negative (generator system!)
                    if load.pf_recap == 0: # power factor is inductive
                        signQG = +1;
                    else:   # power factor is capacitive
                        signQG = -1;
                    
                    qgen = signQG * pgen * tan( acos( load.cosgini ) )
                        
                    p += pgen
                    q += qgen
                    dpf.PrintWarn('In medium voltage load "'+load.loc_name+'" is power feed-in of '+str(load.pgini*correctPrefix(dpf, "k", "M"))+' kW and '+str(qgen*correctPrefix(dpf, "k", "M"))+' kVAr represented. I assume the residual power.')
                
                s = sqrt(p**2 + q**2)
                cosphi_calc = p/s if q>0 else -p/s
                # Negative sign if load is capacitive
                
                cosphi = signQL * load.coslini
                cosErr = abs((cosphi - cosphi_calc)/cosphi) if not cosphi == 0 else inf
                if cosErr > 0.02:
                    cosphi = cosphi_calc
                    dpf.PrintWarn('For medium voltage load "'+load.loc_name+'" the power factor of the residual load deviates more than 2 percent to the given one. Assume the one of the residual load.')
                
                bus = load.bus1.cterm
                #  Check if one of the nodes is an internal node of a substation
                if bus.IsInternalNodeInStation() == 1:
                    mainBuses = bus.GetConnectedMainBuses()
                    if len(mainBuses) == 0:
                        dpf.PrintWarn('Load "'+load.loc_name+'" is connected to an internal node of a substation without being connected main node. Most probably this is not really connected. Check it!')
                        continue
                    elif len(mainBuses) > 1:
                        dpf.PrintError('Node '+load.loc_name+' has more than one main bus. Abort the script!')
                        sys.exit()
                    else:
                        bus = mainBuses[0]
                    
                expMat.append([
                    load.loc_name,  # ID
                    bus.loc_name,  # Knoten
                    s,  # Anschlusswert (Scheinleistung)
                    cosphi,  # cosphi
                    load.elini,  # Jahreswert
                    0,  # DSM
                    "",   # Szenario
                    load.cpArea.loc_name if load.cpArea is not None else "",  # subnet
                    load.cpZone.loc_name if load.cpZone is not None else ""  # voltLvl
                ])
    return (idxWs, expMat)