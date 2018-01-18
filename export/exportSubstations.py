'''
@author: Kittl
'''
def exportSubstations(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Substation"
    elif exportProfile is 3:
        cmpStr = ""
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for substations" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
        
    dpf.PrintPlain('')
    dpf.PrintPlain('###################################')
    dpf.PrintPlain('# Starting to export substations. #')
    dpf.PrintPlain('###################################')
    
    colHead = list();
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    expMat = list()
    expMat.append(colHead)

    substats = dpf.GetCalcRelevantObjects('*.ElmSubstat')
    for substat in substats:
        if exportProfile is 2:
            expMat.append([
                substat.loc_name,  # id
                substat.pArea.loc_name if substat.pArea is not None else "",  # subnet
                substat.pZone.loc_name if substat.pZone is not None else ""  # voltLvl
            ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)