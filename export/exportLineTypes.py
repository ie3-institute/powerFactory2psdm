'''
@author: Kittl
'''
def exportLineTypes(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "LineType"
    elif exportProfile is 3:
        cmpStr = ""
        
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for line types" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 9 indicates the lineType-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('##################################')
    dpf.PrintPlain('# Starting to export line types. #')
    dpf.PrintPlain('##################################')

    expMat = list()
    expMat.append(colHead)

    lineTypes = dpf.GetCalcRelevantObjects('*.TypLne')
    for lineType in lineTypes:
        if exportProfile is 2:
            expMat.append([
                lineType.loc_name,  # id
                lineType.rline,  # positive sequence resistence @ 20 °C
                lineType.xline,  # positive sequence reactance
                lineType.bline,  # positive sequence susceptance
                lineType.sline,  # imax
            ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)