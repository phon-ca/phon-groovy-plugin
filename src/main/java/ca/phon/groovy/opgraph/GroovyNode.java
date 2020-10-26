package ca.phon.groovy.opgraph;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

import javax.script.*;

import ca.phon.app.log.*;
import ca.phon.opgraph.*;
import ca.phon.opgraph.app.*;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.nodes.general.script.*;

/**
 * {@link OpNode} for groovy scripts.
 */
@OpNodeInfo(category = "Script", description = "groovy script node", name = "groovy", showInLibrary = true)
public class GroovyNode extends OpNode implements NodeSettings {
	
	private final static String INIT_FUNCTION = "init";
	private final static String OPERATE_FUNCTION = "operate";
	
	private InputField paramsInputField = new InputField("parameters", "Map of script parameters, these will override node settings.",
			true, true, Map.class);

	private OutputField paramsOutputField = new OutputField("parameters",
			"Parameters used for script, including those entered using the node settings dialog", true, Map.class);
	
	private String scriptText = "";
	
	private GroovyScriptPanel scriptPanel;
	
	private Invocable invocable;
	
	public GroovyNode() {
		super();
		
		putField(paramsInputField);
		putField(paramsOutputField);
		
		reloadFields();
		
		putExtension(NodeSettings.class, this);
	}
	
	private ScriptEngine getScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("groovy");
	}
	
	public String getScriptText() {
		return (this.scriptPanel != null ? this.scriptPanel.getScriptText() : this.scriptText);
	}
	
	public void setScriptText(String scriptText) {
		String oldText = getScriptText();
		this.scriptText = scriptText;
		if(this.scriptPanel != null)
			this.scriptPanel.setScriptText(scriptText);
		if(!oldText.equals(scriptText)) {
			invocable = null;
		}
	}
	
	private Invocable getInvocable() throws ScriptException {
		if(invocable == null) {
			final ScriptEngine engine = getScriptEngine();
			String scriptText = getScriptText();
			engine.eval(scriptText);
			invocable = (Invocable)engine;
		}
		return invocable;
	}
	
	private void reloadFields() {
		final List<InputField> fixedInputs =
				getInputFields().stream().filter( f -> f.isFixed() && f != ENABLED_FIELD ).collect( Collectors.toList() );
		final List<OutputField> fixedOutputs =
				getOutputFields().stream().filter( f -> f.isFixed() && f != COMPLETED_FIELD ).collect( Collectors.toList() );

		// setup fields on temporary node
		final OpNode tempNode = new OpNode("temp", "temp", "temp") {
			@Override
			public void operate(OpContext context) throws ProcessingException {
			}
		};
		for(InputField field:fixedInputs) {
			tempNode.putField(field);
		}
		for(OutputField field:fixedOutputs) {
			tempNode.putField(field);
		}
		final InputFields inputFields = new InputFields(tempNode);
		final OutputFields outputFields = new OutputFields(tempNode);
		try {
			Invocable invocable = (Invocable)getInvocable();
			Object[] params = { inputFields, outputFields };
			invocable.invokeFunction(INIT_FUNCTION, params);
		} catch (ScriptException e) {
			LogUtil.severe( getName() + " (" + getId() + "): " + e.getLocalizedMessage(), e);
		} catch (NoSuchMethodException e) {
		}

		// check inputs
		for(InputField currentInputField:getInputFields().toArray(new InputField[0])) {
			final InputField tempInputField = tempNode.getInputFieldWithKey(currentInputField.getKey());
			if(tempInputField != null) {
				// copy field information
				currentInputField.setDescription(tempInputField.getDescription());
				currentInputField.setFixed(tempInputField.isFixed());
				currentInputField.setOptional(tempInputField.isOptional());
				currentInputField.setValidator(tempInputField.getValidator());
			} else {
				// remove field from node
				removeField(currentInputField);
			}
		}

		final List<String> tempInputKeys = tempNode.getInputFields()
				.stream().map( InputField::getKey ).collect( Collectors.toList() );
		// add new input fields
		for(String tempInputKey:tempInputKeys) {
			final InputField currentInput = getInputFieldWithKey(tempInputKey);
			if(currentInput == null) {
				// add new field to node
				putField(tempInputKeys.indexOf(tempInputKey), tempNode.getInputFieldWithKey(tempInputKey));
			}
		}

		// check outputs
		for(OutputField currentOutputField:getOutputFields().toArray(new OutputField[0])) {
			final OutputField tempOutputField = tempNode.getOutputFieldWithKey(currentOutputField.getKey());
			if(tempOutputField != null) {
				currentOutputField.setDescription(tempOutputField.getDescription());
				currentOutputField.setFixed(tempOutputField.isFixed());
				currentOutputField.setOutputType(tempOutputField.getOutputType());
			} else {
				removeField(currentOutputField);
			}
		}

		final List<String> tempOutputKeys = tempNode.getOutputFields()
				.stream().map( OutputField::getKey ).collect( Collectors.toList() );
		for(String tempOutputKey:tempOutputKeys) {
			final OutputField currentOutput = getOutputFieldWithKey(tempOutputKey);
			if(currentOutput == null) {
				putField(tempOutputKeys.indexOf(tempOutputKey), tempNode.getOutputFieldWithKey(tempOutputKey));
			}
		}
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		try {
			Invocable invocable = getInvocable();
			invocable.invokeFunction(OPERATE_FUNCTION, context);
		} catch(Exception e) {
			throw new ProcessingException(null, e);
		}
	}

	@Override
	public Component getComponent(GraphDocument document) {
		if(scriptPanel == null) {
			scriptPanel = new GroovyScriptPanel(scriptText);
		}
		return scriptPanel;
	}

	@Override
	public Properties getSettings() {
		Properties props = new Properties();
		props.setProperty("scriptText", getScriptText());
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		String scriptText = properties.getProperty("scriptText", "");
		setScriptText(scriptText);
		reloadFields();
	}
	
	

}
