/**
 * The MIT License
 *
 * Copyright (C) 2024 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.swing.app;

import java.nio.file.Path;
import java.util.List;

import javax.swing.JMenuBar;

import org.pf4j.CompoundPluginDescriptorFinder;
import org.pf4j.CompoundPluginLoader;
import org.pf4j.CompoundPluginRepository;
import org.pf4j.DefaultExtensionFinder;
import org.pf4j.DefaultPluginLoader;
import org.pf4j.DefaultPluginManager;
import org.pf4j.DefaultPluginRepository;
import org.pf4j.ExtensionFinder;
import org.pf4j.JarPluginLoader;
import org.pf4j.JarPluginRepository;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;
import org.pf4j.PluginRepository;
import org.pf4j.PluginWrapper;

import io.github.astrapi69.awt.screen.ScreenSizeExtensions;
import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.swing.base.ApplicationPanelFrame;
import io.github.astrapi69.swing.base.BasePanel;
import io.github.astrapi69.swing.plaf.LookAndFeels;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.pf4j.PropertiesPluginDescriptorFinder;

/**
 * The class {@link TemplateApplicationFrame} represents the main frame of the application that sets
 * up and initializes the application window with specific settings and components
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemplateApplicationFrame extends ApplicationPanelFrame<ApplicationModelBean>
{

	/**
	 * The single instance of {@link TemplateApplicationFrame}
	 *
	 * @return single instance of {@link TemplateApplicationFrame} object
	 */
	@Getter
	private static TemplateApplicationFrame instance;

	/** The main application panel */
	ApplicationPanel applicationPanel;

	/** The plugin manager */
	PluginManager pluginManager;

	/**
	 * Constructs a new {@link TemplateApplicationFrame} with the specified title from the resource
	 * bundle
	 */
	public TemplateApplicationFrame()
	{
		super(Messages.getString("mainframe.title"));
	}

	/**
	 * Factory method for create the plugin manager
	 */
	protected PluginManager newPluginManager()
	{
		PluginManager pluginManager = new DefaultPluginManager()
		{

			/**
			 * {@inheritDoc}
			 * <p>
			 * Customizes the extension finder by adding a service provider extension finder
			 */
			protected ExtensionFinder createExtensionFinder()
			{
				DefaultExtensionFinder extensionFinder = (DefaultExtensionFinder)super.createExtensionFinder();
				extensionFinder.addServiceProviderExtensionFinder();
				return extensionFinder;
			}
			@Override
			protected PluginDescriptorFinder createPluginDescriptorFinder() {
				return new CompoundPluginDescriptorFinder()
						.add(new PropertiesPluginDescriptorFinder())
						.add(new ManifestPluginDescriptorFinder());
			}

			@Override
			protected PluginRepository createPluginRepository() {
				return new CompoundPluginRepository()
						.add(new DefaultPluginRepository(getPluginsRoot()))
						.add(new JarPluginRepository(getPluginsRoot()));
			}

			protected PluginClassLoader createPluginClassLoader(Path pluginPath, PluginDescriptor pluginDescriptor) {
				return new PluginClassLoader(this, pluginDescriptor, getClass().getClassLoader(), true);
			}
			@Override
			protected PluginLoader createPluginLoader() {
				PluginLoader superPluginLoader = super.createPluginLoader();
				return new CompoundPluginLoader()
						.add(superPluginLoader)
						.add(new DefaultPluginLoader(this))
						.add(new JarPluginLoader(this));
			}

		};
		return pluginManager;
	}

	/**
	 * Starts and loads all plugins of the application
	 */
	protected void startAndLoadAllPlugins()
	{
		pluginManager.loadPlugins();
		pluginManager.startPlugins();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onBeforeInitialize()
	{
		if (instance == null)
		{
			instance = this;
		}
		pluginManager = newPluginManager();
		// initialize model and model object
		ApplicationModelBean applicationModelBean = ApplicationModelBean.builder()
			.title(Messages.getString("mainframe.title")).build();
		setModel(BaseModel.of(applicationModelBean));
		super.onBeforeInitialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onAfterInitialize()
	{
		super.onAfterInitialize();
		startAndLoadAllPlugins();

		List<Class<?>> extensionClasses = pluginManager.getExtensionClasses("menu-plugin");


		List<PluginWrapper> plugins = pluginManager.getPlugins();
		plugins.forEach(plugin -> {
			System.out.println(
				"Plugin: " + plugin.getPluginId() + " is in state: " + plugin.getPluginState());
		});

		setTitle(Messages.getString("mainframe.title"));
		setDefaultLookAndFeel(LookAndFeels.NIMBUS, this);
		this.setSize(ScreenSizeExtensions.getScreenWidth(), ScreenSizeExtensions.getScreenHeight());
	}

	@Override
	protected JMenuBar newJMenuBar()
	{

		return super.newJMenuBar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String newIconPath()
	{
		return Messages.getString("global.icon.app.path");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BasePanel<ApplicationModelBean> newMainComponent()
	{
		return new ApplicationPanel(getModel());
	}
}
