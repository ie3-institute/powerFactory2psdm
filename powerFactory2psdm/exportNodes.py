'''
@author: Kittl
'''
from exportHelpers import correctPrefix
from math import acos, tan

def exportNodes(dpf, comLdf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Node"
    elif exportProfile is 3:
        cmpStr = "Knoten"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for nodes" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 1 indicates the nodes-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#############################')
    dpf.PrintPlain('# Starting to export nodes. #')
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
            ['Knotendaten']
        ] + expMat

    slackBuses = list()
    scaleStorHeating = comLdf.scPnight / 100    # Scaling factor for storage heaters comes as percentage
    nodes = dpf.GetCalcRelevantObjects('*.ElmTerm')
    for node in nodes:
        if exportProfile is 2:
            if node.iUsage is 0:
                usage = "busbar"
            elif node.iUsage is 1:
                usage = "muff"
            elif node.iUsage is 2:
                usage = "auxiliary node"
            else:
                usage = ""

            expMat.append([
                node.loc_name,  # id
                usage,  # type
                node.vtarget,   # vSetp
                node.uknom,    # vR
                node.cpSubstat.loc_name if node.cpSubstat is not None else "",  # substation
                node.cpArea.loc_name if node.cpArea is not None else "",  # subnet
                node.cpZone.loc_name if node.cpZone is not None else ""  # voltLvl
            ])
        elif exportProfile is 3:
            # Do not export nodes, which are internal nodes in substations
            if not node.IsInternalNodeInStation() == 1:
                # Determine the connected load and genration power for each node
                pload = 0
                qload = 0
                pgen = 0
                qgen = 0
                usetp = 0
                bustype = ""
                conElms = node.GetConnectedElements()
                for conElm in conElms:
                    if conElm.outserv == 1:
                        continue
                    
                    if conElm.GetClassName() == "ElmLod":                    
                        pload += conElm.plini_a * correctPrefix(dpf, "M", "PQS");
                        qload += conElm.qlini_a * correctPrefix(dpf, "M", "PQS");
                    elif conElm.GetClassName() == "ElmLodlv":
                        p = conElm.plini_a * correctPrefix(dpf, "k", "k");
                    
                        # Distinguish between inductive and capacitive power factor
                        # The capacitive factor is defined as negative
                        if conElm.pf_recap == 0: # power factor is inductive
                            signQL = +1;
                        else:   # power factor is capacitive
                            signQL = -1;
                        
                        q = signQL * p * tan( acos( conElm.coslini ) )
                        
                        if not conElm.pnight == 0:
                            # Get the correct scaling factor, which is composed of an individual scaling factor, a scaling factor of the load flow calculation object and a scaling factor of the zone
                            loadScale = conElm.cpZone.curscale * scaleStorHeating
                            p += conElm.pnight * correctPrefix(dpf, "k", "k") * loadScale   # Storage Heater
                    
                        # Consider distinct households
                        if not conElm.cNrCust == 0:
                            # Get the correct scaling factor, which is composed of an individual scaling factor, a scaling factor of the load flow calculation object and a scaling factor of the zone
                            loadScale = comLdf.scLoadFac/100 * conElm.cpZone.curscale
                            p += conElm.cNrCust * conElm.cPrCust * loadScale * correctPrefix(dpf, "k", "k")
                            q += conElm.cNrCust * conElm.cPrCust * loadScale * tan( acos( conElm.ccosphi ) ) * correctPrefix(dpf, "k", "k")
                        
                        pload += (p * correctPrefix(dpf, "M", "k"))
                        qload += (q * correctPrefix(dpf, "M", "k"))
                    elif conElm.GetClassName() == "ElmLodmv":
                        pl = conElm.plini_a
                        
                        # Distinguish between inductive and capacitive power factor
                        # The capacitive factor is defined as negative
                        if conElm.pf_recap == 0: # power factor is inductive
                            signQ = +1
                        else:   # power factor is capacitive
                            signQ = -1
                        
                        ql = signQ * pl * tan( acos( conElm.coslini ) )
                        
                        if not conElm.sgini == 0:
                            pg = conElm.pgini_a
                                
                            # Distinguish between inductive and capacitive power factor
                            # The inductive factor is defined as negative (generator system!)
                            if conElm.pfg_recap == 0: # power factor is inductive
                                signQ = -1
                            else:   # power factor is capacitive
                                signQ = +1
                            
                            qg = signQ * pg * tan( acos( conElm.cosgini ) )
                        
                        pload += (pl * correctPrefix(dpf, "M", "M"))
                        qload += (ql * correctPrefix(dpf, "M", "M"))
                        pgen -= (pg * correctPrefix(dpf, "M", "M"))
                        qgen -= (qg * correctPrefix(dpf, "M", "M"))
                    elif conElm.GetClassName() == "ElmGenstat" or conElm.GetClassName() == "ElmPvsys" or conElm.GetClassName() == "ElmSym":
                        # For all generators
                        if conElm.GetClassName() == "ElmSym":
                            pgen += conElm.pgini_a * correctPrefix(dpf, "M", "M")
                            qgen += conElm.qgini_a * correctPrefix(dpf, "M", "M")
                        else:
                            pgen += conElm.pgini_a * correctPrefix(dpf, "M", "M")
                            qgen += conElm.qgini_a * correctPrefix(dpf, "M", "M")
                        # At this point I assume that the external grid has better capabilities to obtain the target voltage in comparison to a generator
                        # With other words: The target voltage of an external net has higher priority as the one of a generator
                        if conElm.ip_ctrl == 1:
                            # Element is a slack bus and regulates voltage
                            bustype = "SL"
                            usetp = conElm.usetp if usetp == 0 else usetp
                        if conElm.av_mode == "constv":
                            # Element regulates voltage
                            bustype = "PV" if not bustype == "SL" else bustype
                            usetp = conElm.usetp if usetp == 0 else usetp
                    elif conElm.GetClassName() == "ElmXnet":
                        # For all external nets
                        bustype = conElm.bustp
                        if bustype == "SL":                            
                            if conElm.uset_mode == 0:
                                # The voltage setpoint is given locally
                                usetp = conElm.usetp
                            elif conElm.uset_mode == 1:
                                # The voltage is given in a remote node
                                if conElm.cpCtrlNode.loc_name == node.loc_name:
                                    # The target node is the same as the current node
                                    usetp = conElm.cpCtrlNode.usetp
                                else:
                                    # The target node is not the same as the current node
                                    dpf.PrintWarn('The external net '+conElm.loc_name+' regulates the voltage at node '+conElm.cpCtrlNode.loc_name+', which is not the current node. Cant determine a correct setpoint voltage. Instead leave it at 1 p.u.')
                        elif bustype == "PV":
                            pgen += conElm.pgini
                            if conElm.uset_mode == 0:
                                # The voltage setpoint is given locally
                                usetp = conElm.usetp
                            elif conElm.uset_mode == 1:
                                # The voltage is given in a remote node
                                if conElm.cpCtrlNode.loc_name == node.loc_name:
                                    # The target node is the same as the current node
                                    usetp = conElm.cpCtrlNode.usetp
                                else:
                                    # The target node is not the same as the current node
                                    dpf.PrintWarn('The external net '+conElm.loc_name+' regulates the voltage at node '+conElm.cpCtrlNode.loc_name+', which is not the current node. Cant determine a correct setpoint voltage. Instead leave it at 1 p.u.')
                        elif bustype == "PQ":
                            pgen += conElm.pgini
                            qgen += conElm.qgini
                            
                    
                bustype = "PQ" if (bustype == "" or not (bustype == "SL" or bustype == "PV")) else bustype
                usetp = 1 if usetp == 0 else usetp
                    
                # Remember the slack bus for later topology check
                if bustype == "SL":
                    if not len(slackBuses) == 0:
                        dpf.PrintWarn('There were already nodes defined as slack buses. Separate the project to achieve one slack bus per project.')
                    slackBuses.append(node)
                        
                expMat.append([
                    node.loc_name, # Knotenname
                    bustype,    # Spgs-Regelung
                    usetp,  # Spg_SollWert
                    pgen,   # P_Gen
                    qgen,   # Q_Gen
                    pload,  # P_Load
                    qload,  # Q_Load
                    node.uknom,    # U_nenn
                    node.cpArea.loc_name if node.cpArea is not None else "",  # Teilnetz
                    node.cpZone.loc_name if node.cpZone is not None else ""  # Spannungsebene
                ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    
    # The iteration went through all nodes
    if exportProfile is 3:
        # Eliminate all those nodes, which are not connected to the slack bus
        if len(slackBuses) == 0:
            dpf.PrintError('There is no slack bus defined!')
            exit(1)
            
        dpf.PrintInfo('The following node have no connection to one of the following slack buses and are being ignored.')
        dpf.PrintPlain('Available slack buses:')
        dpf.PrintPlain('----------------------')
        for slackBus in slackBuses:
            dpf.PrintPlain('* '+slackBus.loc_name)
        dpf.PrintPlain('Nodes to be ignored:')
        dpf.PrintPlain('--------------------')
        
        # Shift the line index by three, because there is a preamble in the list
        cntNode = 3
        nrNodes = len(expMat)-1
        while cntNode <= nrNodes:
            # Get the node object
            node = dpf.GetCalcRelevantObjects(expMat[cntNode][0]+'.ElmTerm')
            if len(node) > 1:
                dpf.PrintWarn('The node with the name "'+str(expMat[cntNode][0])+'" is not unique ('+str(len(node))+' instances)!')
            node = node[0]
            
            # Check if there is any connection to one of the slack buses
            hasConnection = False
            for slackBus in slackBuses:
                topCollection = node.GetMinDistance(slackBus)
                hasConnection = False if ( topCollection[0] < 0 and not hasConnection) else True
            
            if not hasConnection:
                dpf.PrintPlain('* '+node.loc_name)
                del expMat[cntNode]
                nrNodes -= 1
                cntNode -= 1    # Reduce counter here. If the node is deleted, you have to stay on the same array position
            cntNode += 1
                
    return (idxWs, expMat)