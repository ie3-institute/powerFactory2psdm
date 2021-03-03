'''
@author: Kittl
'''
def exportStorageTypes(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "StorageType"
    elif exportProfile is 3:
        cmpStr = ""
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for storage types" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 11 indicates the storageType-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#####################################')
    dpf.PrintPlain('# Starting to export storage types. #')
    dpf.PrintPlain('#####################################')

    expMat = list()
    expMat.append(colHead)

# ~~~~~~~~ Implement storage export! ~~~~~~~~
#     reses = dpf.GetCalcRelevantObjects('*.ElmGenstat')
#     for res in reses:
#         if exportProfile is 2:
#             expMat.append([
#                 res.loc_name,  # id
#                 res.bus1.cterm.loc_name,  # node
#                 res.cCategory,  # type
#                 "",  # pvType
#                 "",  # wecType
#                 "",  # bmType#
#                 res.pgini,  # pload
#                 res.cosgini,  # cosphi
#                 res.sgn,  # sR
#                 res.cpArea.loc_name if res.cpArea is not None else "",  # subnet
#                 res.cpZone.loc_name if res.cpZone is not None else ""  # voltLvl
#             ])
#         else:
#             dpf.PrintError("This export profile isn't implemented yet.")
#             exit(1)
    return (idxWs, expMat)