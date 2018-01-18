'''
@author: Kittl
'''
def exportStorages(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Storage"
    elif exportProfile is 3:
        cmpStr = "Speicher"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for storages" )+' defined. Skip this one!')
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
    dpf.PrintPlain('################################')
    dpf.PrintPlain('# Starting to export storages. #')
    dpf.PrintPlain('################################')

    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['Speicher-Daten']
        ] + expMat

    storages = dpf.GetCalcRelevantObjects('*.ElmGenstat')
    for storage in storages:
        if exportProfile is 2:
            break
        if exportProfile is 3:
            if storage.cCategory == 'Batterie':
                expMat.append([
                    storage.loc_name,    # ID
                    storage.bus1.cterm.loc_name,  # Knoten
                    storage.cCategory, # Typ
                    "", # Verhalten
                    storage.cpArea.loc_name if storage.cpArea is not None else "",  # Teilnetz
                    storage.cpZone.loc_name if storage.cpZone is not None else ""  # Spannungsebene
                ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)