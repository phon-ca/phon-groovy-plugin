package ca.phon.groovy.opgraph;

import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.exceptions.ProcessingException;

@OpNodeInfo(category = "Script", description = "groovy script node", name = "groovy", showInLibrary = true)
public class GroovyNode extends OpNode {
	
	public GroovyNode() {
		super();
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		
	}

}
