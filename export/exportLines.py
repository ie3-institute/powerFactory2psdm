'''
@author: Kittl
'''
from exportHelpers import correctPrefix
import sys

def exportLines(dpf, exportProfile, tables, colHeads):
    # Get the index in the list of worksheets
    if exportProfile is 2:
        cmpStr = "Line"
    elif exportProfile is 3:
        cmpStr = "Leitungen"
    
    idxWs = [idx for idx,val in enumerate(tables[exportProfile-1]) if val == cmpStr]
    if not idxWs:
        dpf.PrintPlain('')
        dpf.PrintInfo('There is no worksheet '+( cmpStr if not cmpStr == "" else "for lines" )+' defined. Skip this one!')
        return (None, None)
    elif len(idxWs) > 1:
        dpf.PrintError('There is more than one table with the name '+cmpStr+' defined. Cancel this script.')
        exit(1)
    else:
        idxWs = idxWs[0]
    
    colHead = list();
    # Index 2 indicates the lines-Worksheet
    for cHead in colHeads[exportProfile-1][idxWs]:
        colHead.append(str(cHead.name))

    dpf.PrintPlain('')
    dpf.PrintPlain('#############################')
    dpf.PrintPlain('# Starting to export lines. #')
    dpf.PrintPlain('#############################')
    
    expMat = list()
    expMat.append(colHead)
    
    if exportProfile is 3:
        # Write preamble to the file
        expMat = [
            [
                dpf.GetActiveProject().loc_name,    # Projektname
                dpf.GetActiveStudyCase().loc_name   # Name des Berechnungsfalls
            ],
            ['Leitungsdaten']
        ] + expMat

    lines = dpf.GetCalcRelevantObjects('*.ElmLne')
    if exportProfile is 3:
        lines += dpf.GetCalcRelevantObjects('*.RelFuse')
    for line in lines:
        # GetCalcRelevantObjects seems not to work properly for lines out of service...
        if line.outserv == 1:
            continue
        
        if exportProfile is 2:
            expMat.append([
                line.loc_name,  # id
                line.bus1.cterm.loc_name,  # nodeA
                line.bus2.cterm.loc_name,  # nodeB
                line.typ_id.loc_name if line.typ_id is not None else "",  # type
                line.dline*correctPrefix(dpf, "k", "l"),  # length
                line.nlnum,  # nrParallel
                line.cpArea.loc_name if line.cpArea is not None else "",  # subnet
                line.cpZone.loc_name if line.cpZone is not None else ""  # voltLvl
            ])
        elif exportProfile is 3:
            if line.GetClassName() == 'ElmLne':
                # Get frequently used variables
                laenge = line.dline
                nrParallel = line.nlnum
                lineType = line.typ_id                    
                bLine = 0   # susceptance of the line
                busA = line.bus1
                busB = line.bus2
                
                # Only export those lines, whose switches are closed at both ends of the line
                cubicEquipA = busA.GetChildren(0)
                cubicEquipB = busB.GetChildren(0)
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
                    # There is some equipment inside the Cubical - Let's see what it is
                    for cubicEquip in cubicEquipB[0]:
                        if cubicEquip.GetClassName() == 'StaSwitch':
                            stateSwB = cubicEquip.isclosed
                            swBFound = True
                    if not swBFound:
                        # If no switch is found, there cannot be any switch which is open --> Behave like there is a closed switch
                        stateSwB = 1
                        swBFound = True
                    
                if not stateSwA == 1 or not stateSwB == 1:
                    # If at least one switch is open the line doesn't get exported
                    dpf.PrintPlain('Line "'+line.loc_name+'" is ignored, because it is not connected at both ends and therefore doesnt participate in the topology.')
                    continue
                
                busA = busA.cterm
                busB = busB.cterm
                
                #  Check if one of the nodes is an internal node of a substation
                if busA.IsInternalNodeInStation() == 1:
                    mainBuses = busA.GetConnectedMainBuses()
                    if len(mainBuses) == 0:
                        dpf.PrintWarn('Line "'+line.loc_name+'" ends at an internal node of a substation without being connected main node. Most probably this is an open end. Check it!')
                        continue
                    elif len(mainBuses) > 1:
                        dpf.PrintError('Node '+busA.loc_name+' has more than one main bus. Abort the script!')
                        sys.exit()
                    else:
                        busA = mainBuses[0]
                if busB.IsInternalNodeInStation() == 1:
                    mainBuses = busB.GetConnectedMainBuses()
                    if len(mainBuses) == 0:
                        dpf.PrintWarn('Line "'+line.loc_name+'" ends at an internal node of a substation without being connected main node. Most probably this is an open end. Check it!')
                        continue
                    elif len(mainBuses) > 1:
                        dpf.PrintError('Node '+busB.loc_name+' has more than one main bus. Abort the script!')
                        sys.exit()
                    else:
                        busB = mainBuses[0]
                
                # Check if line segments are implemented
                if lineType is None:
                    lineSecs = line.GetChildren(0)
                    if not len(lineSecs[0]) == 0:
                        for lineSec in lineSecs[0]:
                            # Check whether the current child is a line segment (could also be a line load)
                            if lineSec.GetClassName() == "ElmLnesec":
                                bLine += lineSec.typ_id.bline * lineSec.dline * correctPrefix(dpf, "k", "l")
                    else:
                        dpf.PrintError('There is no line type implemented for line '+line.loc_name+'. Abort the script.')
                        sys.exit()
                else:
                    bLine = lineType.bline * nrParallel * laenge * correctPrefix(dpf, "k", "l")
                
                # Get the correct line parameters for a single cable, not the sum of parallel cables
                # 1/rGes = nrParallel * 1/rLine
                rLine = nrParallel * line.R1
                xLine = nrParallel * line.X1

                expMat.append([
                    line.loc_name,  # Leitungsname
                    busA.loc_name,  # KnotenA
                    busB.loc_name,  # KnotenB
                    rLine/(laenge*correctPrefix(dpf, "k", "l")),   # R-Belag
                    xLine/(laenge*correctPrefix(dpf, "k", "l")),   # X-Belag
                    bLine/(laenge*correctPrefix(dpf, "k", "l")),   # B-Belag
                    laenge*correctPrefix(dpf, "k", "l"), # Laenge
                    nrParallel, # Anzahl
                    line.Inom_a / nrParallel * correctPrefix(dpf, "", "k"),    # Inenn
                    line.cpArea.loc_name if line.cpArea is not None else "",  # Teilnetz
                    line.cpZone.loc_name if line.cpZone is not None else ""  # Spannungsebene
                ])
            else: # Hier nun Dummy-Leitungen für Sicherungen einbauen.
                if line.on_off == 1:
                    expMat.append([
                        line.loc_name,  # Leitungsname
                        line.bus1.cterm.loc_name,  # KnotenA
                        line.bus2.cterm.loc_name,  # KnotenB
                        0,   # R-Belag
                        0,   # X-Belag
                        0,   # B-Belag
                        0.001, # Laenge
                        1, # Anzahl
                        5 * correctPrefix(dpf, "", "k"),    # Inenn
                        line.cpArea.loc_name if line.cpArea is not None else "",  # Teilnetz
                        line.cpZone.loc_name if line.cpZone is not None else ""  # Spannungsebene
                    ])
                else:
                    continue
        else:
            dpf.PrintError("This export profile isn't implemented yet.")
            exit(1)
    return (idxWs, expMat)