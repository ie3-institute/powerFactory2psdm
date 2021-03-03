'''
@author: Kittl
'''
from exportHelpers import correctPrefix
import sys
def exportTransformers(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Transformer"
    elif exportProfile is 3:
        cmpStr = "Trafos"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for transformers" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 3 indicates the transformer-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('####################################')
    dpf.PrintPlain('# Starting to export transformers. #')
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
            ['Trafodaten']
        ] + expMat

    transformers = dpf.GetCalcRelevantObjects('*.ElmTr2')
    # If necessary advance the selection by other transformer elements (three-winding etc.)
    for transformer in transformers:
        if exportProfile is 2:
            expMat.append([
                transformer.loc_name,  # id
                transformer.bushv.cterm.loc_name,  # nodeA
                transformer.buslv.cterm.loc_name,  # nodeA
                transformer.typ_id.loc_name if transformer.typ_id is not None else "",  # type
                transformer.ntnum,  # nrParallel
                transformer.nntap,  # tappos
                transformer.nntap,  # tapposDef
                1 if transformer.ntrcn is 1 or transformer.c_ptapc is not None else 0,  #autoTap
                transformer.cpArea.loc_name if transformer.cpArea is not None else "",  # subnet
                transformer.cpZone.loc_name if transformer.cpZone is not None else ""  # voltLvl
            ])
        elif exportProfile is 3:
            busHV = transformer.bushv
            busLV = transformer.buslv
            # Only export those transformers, whose switches are closed at both ends of the transformer
            cubicEquipA = busHV.GetChildren(0)
            cubicEquipB = busLV.GetChildren(0)
            swAFound = False
            swBFound = False
            # Preset the switches state here
            # If there is no switch in the Cubicle this value won't be altered, because there cannot be a switch being opened
            stateSwA = 1
            stateSwB = 1            
            if not len(cubicEquipA[0]) == 0:
                # There is some equipment inside the Cubicle - Let's see what it is
                for cubicEquip in cubicEquipA[0]:
                    if cubicEquip.GetClassName() == 'StaSwitch':
                        stateSwA = cubicEquip.isclosed
                        swAFound = True
                if not swAFound:
                    # If no switch is found, there cannot be any switch which is open --> Behave like there is a closed switch
                    stateSwA = 1
                    swAFound = True
            if not len(cubicEquipB[0]) == 0:
                # There is some equipment inside the Cubicle - Let's see what it is
                for cubicEquip in cubicEquipB[0]:
                    if cubicEquip.GetClassName() == 'StaSwitch':
                        stateSwB = cubicEquip.isclosed
                        swBFound = True
                if not swBFound:
                    # If no switch is found, there cannot be any switch which is open --> Behave like there is a closed switch
                    stateSwB = 1
                    swBFound = True
                
            if not stateSwA == 1 or not stateSwB == 1:
                # If at least one switch is open the transformer doesn't get exported
                dpf.PrintPlain('Transformer "'+transformer.loc_name+'" is ignored, because it is not connected at both ends and therefore doesnt participate in the topology.')
                continue
            
            busHV = busHV.cterm
            busLV = busLV.cterm
            
            #  Check if one of the nodes is an internal node of a substation
            if busHV.IsInternalNodeInStation() == 1:
                mainBuses = busHV.GetConnectedMainBuses()
                if len(mainBuses) == 0:
                    dpf.PrintWarn('Node '+busHV.loc_name+' is an internal node of a substation, but has no main bus. Check this node!')
                elif len(mainBuses) > 1:
                    dpf.PrintError('Node '+busHV.loc_name+' has more than one main bus. Abort the script!')
                    sys.exit()
                else:
                    busHV = mainBuses[0]
            if busLV.IsInternalNodeInStation() == 1:
                mainBuses = busLV.GetConnectedMainBuses()
                if len(mainBuses) == 0:
                    dpf.PrintWarn('Node '+busLV.loc_name+' is an internal node of a substation, but has no main bus. Check this node!')
                elif len(mainBuses) > 1:
                    dpf.PrintError('Node '+busLV.loc_name+' has more than one main bus. Abort the script!')
                    sys.exit()
                else:
                    busLV = mainBuses[0]
            
            
            if transformer.typ_id.itapch == 1 and transformer.typ_id.itapch2 == 1:
                dpf.PrintWarn('The transformer '+transformer.loc_name+' has two tap changers. I neglect the one on the low voltage side!')
            tapSide = 0
            if transformer.typ_id.itapch == 1:
                tapSide = transformer.typ_id.tap_side
                dV = transformer.typ_id.dutap
                dphi = transformer.typ_id.phitr
                tapNeu = transformer.typ_id.nntap0
                tapMin = transformer.typ_id.ntpmn
                tapMax = transformer.typ_id.ntpmx
            elif transformer.typ_id.itapch2 == 1:
                tapSide = transformer.typ_id.tap_side2
                dV = transformer.typ_id.dutap2
                dphi = transformer.typ_id.phitr2
                tapNeu = transformer.typ_id.nntap02
                tapMin = transformer.typ_id.ntpmn2
                tapMax = transformer.typ_id.ntpmx2
            else:
                tapSide = 0
                dV = 0
                dphi = 0
                tapNeu = 0
                tapMin = 0
                tapMax = 0
                
            
            expMat.append([
                transformer.loc_name,  # Trafoname
                busHV.loc_name,  # KnotenA
                busLV.loc_name,  # KnotenB
                transformer.typ_id.strn*correctPrefix(dpf, "M", "M"),    # Smax
                transformer.typ_id.utrn_h,  # U_OS
                transformer.typ_id.utrn_l,  # U_US
                transformer.typ_id.uktr,    # u_k
                transformer.typ_id.pcutr*correctPrefix(dpf, "k", "k"),   # P_Kuperverluste
                transformer.typ_id.pfe*correctPrefix(dpf, "k", "k"),  # P_Eisenverluste
                transformer.typ_id.curmg,   # Leerlaufstrom
                transformer.nntap,   # Tapposition
                tapSide,    # Tapseite
                dV, # du
                dphi,   # dphi
                tapNeu, # tap neutrale Position
                tapMax, # Tap_mx
                tapMin, # Tap_mn
                transformer.ntnum,  # parallele Anzahl
                1 if transformer.ntrcn is 1 or transformer.c_ptapc is not None else 0,  # Auto_Stufung
                busLV.cpArea.loc_name if busLV.cpArea is not None else "",  # subnet
                busLV.cpZone.loc_name if busLV.cpZone is not None else ""  # voltLvl
            ])
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)