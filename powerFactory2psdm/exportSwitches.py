'''
@author: Kittl
'''
def exportSwitches(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Switch"
    elif exportProfile is 3:
        cmpStr = "Schalter"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for switches" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 4 indicates the switches-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('####################################')
    dpf.PrintPlain('# Starting to export switches. #')
    dpf.PrintPlain('####################################')
    
    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['Schalterdaten']
        ] + expMat

    # At the moment it is just checked, if the edge object is out of service or not. In future version the switches
    # themselves have to be checked!
    edges = dpf.GetCalcRelevantObjects('*.ElmLne')
    edges += dpf.GetCalcRelevantObjects('*.ElmTr2')
    for edge in edges:
        if exportProfile is 2:
            if edge.GetClassName() == "ElmLne":
                nodeA = edge.bus1.cterm.loc_name
                nodeB = edge.bus2.cterm.loc_name
            elif edge.GetClassName() == "ElmTr2":
                nodeA = edge.bushv.cterm.loc_name
                nodeB = edge.buslv.cterm.loc_name
            else:
                nodeA = ""
                nodeB = ""

            expMat.append([
                edge.loc_name,  # id
                nodeA,  # nodeA
                nodeB,  # nodeB
                0 if edge.outserv is 1 else 1,  # cond
                0 if edge.outserv is 1 else 1,  # condDef
                edge.cpArea.loc_name if edge.cpArea is not None else "",  # subnet
                edge.cpZone.loc_name if edge.cpZone is not None else ""  # voltLvl
            ])
        elif exportProfile is 3:
            # There should no data be exported.
            break
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)