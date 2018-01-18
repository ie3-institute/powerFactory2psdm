'''
@author: Kittl
'''
def exportExternalNets(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "ExternalNet"
    elif exportProfile is 3:
        cmpStr = ""
            
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for external nets" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 8 indicates the externalNet-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#####################################')
    dpf.PrintPlain('# Starting to export external nets. #')
    dpf.PrintPlain('#####################################')

    expMat = list()
    expMat.append(colHead)

    externalNets = dpf.GetCalcRelevantObjects('*.ElmXNet')
    for externalNet in externalNets:
        if exportProfile is 2:
            # Get voltage setpoint from connected node, when this is chosen in PowerFactory
            if externalNet.uset_mode is 1:
                vtarget = externalNet.cpCtrlNode.vtarget
                if not externalNet.cpCtrlNode.loc_name == externalNet.bus1.cterm.loc_name:
                    dpf.PrintWarn('The external net '+externalNet.loc_name+' regulates the voltage at node '+externalNet.cpCtrlNode.loc_name+', which is not the current node.')
            else:
                vtarget = externalNet.usetp

            expMat.append([
                externalNet.loc_name,  # id
                externalNet.bus1.cterm.loc_name,  # node
                vtarget,  # vSetp
                externalNet.phiini,  # phiSetp
                externalNet.cpArea.loc_name if externalNet.cpArea is not None else "",  # subnet
                externalNet.cpZone.loc_name if externalNet.cpZone is not None else ""  # voltLvl
            ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)