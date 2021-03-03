'''
@author: Kittl
'''
def exportEV(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = ""
    elif exportProfile is 3:
        cmpStr = "EV"
            
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for electic vehicles" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#########################################')
    dpf.PrintPlain('# Starting to export electric vehicles. #')
    dpf.PrintPlain('#########################################')

    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['EV-Daten']
        ] + expMat

    evs = dpf.GetCalcRelevantObjects('*.ElmGenstat')
    for ev in evs:
        if exportProfile is 3:
            if ev.cCategory == 'Sonstige':
                expMat.append([
                    ev.loc_name,    # ID
                    ev.bus1.cterm.loc_name,  # Knoten
                    ev.cCategory, # Typ
                    ev.cpArea.loc_name if ev.cpArea is not None else "",  # Teilnetz
                    ev.cpZone.loc_name if ev.cpZone is not None else ""  # Spannungsebene
                ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)