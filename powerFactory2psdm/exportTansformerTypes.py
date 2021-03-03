'''
@author: Kittl
'''
from exportHelpers import correctPrefix
def exportTransformerTypes(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "TransformerType"
    elif exportProfile is 3:
        cmpStr = ""
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for transformer types" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 10 indicates the transformerType-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#########################################')
    dpf.PrintPlain('# Starting to export transformer types. #')
    dpf.PrintPlain('#########################################')

    expMat = list()
    expMat.append(colHead)

    transformerTypes = dpf.GetCalcRelevantObjects('*.TypTr2')
    for transformerType in transformerTypes:
        if exportProfile is 2:
            if transformerType.itapch is 1:
                # Tap changer one is active
                tappable = transformerType.itapch
                tapside = "HV" if transformerType.tap_side is 0 else "LV"
                dV = transformerType.dutap
                dPhi = transformerType.phitr
                tapNeutr = transformerType.nntap0
                tapMin = transformerType.ntpmn
                tapMax = transformerType.ntpmx
            elif transformerType.itapch2 is 1:
                # tap changer two is active
                tappable = transformerType.itapch2
                tapside = "HV" if transformerType.tap_side2 is 0 else "LV"
                dV = transformerType.dutap2
                dPhi = transformerType.phitr2
                tapNeutr = transformerType.nntap02
                tapMin = transformerType.ntpmn2
                tapMax = transformerType.ntpmx2
            else:
                # there is no tap changer active
                tappable = 0
                tapside = ""
                dV = 0
                dPhi = 0
                tapNeutr = 0
                tapMin = 0
                tapMax = 0

            expMat.append([
                transformerType.loc_name,  # id
                transformerType.strn*correctPrefix(dpf, "k", "PQS"),  # sR
                transformerType.utrn_h,  # vHV
                transformerType.utrn_l,  # vLV
                transformerType.nt2ag*30, # va0
                transformerType.uktr,  # vImp
                transformerType.pcutr*correctPrefix(dpf, "k", "PQS"),  # pCu
                transformerType.pfe*correctPrefix(dpf, "k", "PQS"),  # pCu
                transformerType.curmg,  # iNoLoad
                tappable,  # tappable
                tapside,  # tapside
                dV,  # dV
                dPhi,  # dPhi
                tapNeutr,  #tapNeutr
                tapMin,  # tapMin
                tapMax  # tapMax
            ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)