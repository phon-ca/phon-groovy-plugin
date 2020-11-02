package ca.phon.groovy.opgraph;

import java.awt.*;

import ca.phon.app.hooks.*;
import ca.phon.app.log.*;
import ca.phon.opgraph.app.components.canvas.*;
import ca.phon.plugin.*;
import ca.phon.util.icons.*;

@PhonPlugin(name="phon-groovy-plugin")
public class GroovyPluginStarupHook implements PhonStartupHook, IPluginExtensionPoint<PhonStartupHook> {

	@Override
	public void startup() throws PluginException {
		addNodeStyles();
	}

	private void addNodeStyles() {
		LogUtil.info("Groovy plug-in installing node style");
		final NodeStyle groovyStyle = new NodeStyle(NodeStyle.DEFAULT);
		groovyStyle.NodeIcon = IconManager.getInstance().getSystemIconForFileType("groovy", "mimetypes/text-x-script", IconSize.SMALL);
		groovyStyle.NodeBackgroundColor = new Color(125, 183, 255, 200);
		groovyStyle.NodeNameTopColor = new Color(125, 183, 255, 255);
		groovyStyle.NodeNameBottomColor = new Color(100, 142, 237, 255);
		NodeStyle.installStyleForNode(GroovyNode.class, groovyStyle);
	}

	@Override
	public Class<?> getExtensionType() {
		return PhonStartupHook.class;
	}

	@Override
	public IPluginExtensionFactory<PhonStartupHook> getFactory() {
		return (args) -> this;
	}
	
}
