'''
@author: Kittl
'''
def exportHP(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = ""
    elif exportProfile is 3:
        cmpStr = "WP"
            
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for heat pumps" )+' defined. Skip this one!')
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
    dpf.PrintPlain('##################################')
    dpf.PrintPlain('# Starting to export heat pumps. #')
    dpf.PrintPlain('##################################')

    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['WP-Daten']
        ] + expMat

    hps = dpf.GetCalcRelevantObjects('*.ElmGenstat')
    for hp in hps:
        if exportProfile is 3:
            if hp.cCategory == 'Brennstoffzelle':
                expMat.append([
                    hp.loc_name,    # ID
                    hp.bus1.cterm.loc_name,  # Knoten
                    hp.cCategory, # Typ
                    hp.cpArea.loc_name if hp.cpArea is not None else "",  # Teilnetz
                    hp.cpZone.loc_name if hp.cpZone is not None else ""  # Spannungsebene
                ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)