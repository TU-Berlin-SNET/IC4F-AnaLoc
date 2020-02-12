package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model;


/**
 * Description:  WarningType combined warningTypeNo with warningMessage
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class WarningType {
	public int typeNo;

	private static final String warningMessages[] = {
			"",													//	0	->	no warning
			"Forbidden area warning",		//	1
			"Low velocity warning"			//	2
	};

	public WarningType(){
		this.typeNo	= 0;
	}

	public WarningType(int typeNo){
		if(typeNo >= 0 && typeNo<warningMessages.length) {
			this.typeNo = typeNo;
		}
		else {
			this.typeNo = 0;
		}
	}

	public String message(){
		return warningMessages[this.typeNo];
	};
}
