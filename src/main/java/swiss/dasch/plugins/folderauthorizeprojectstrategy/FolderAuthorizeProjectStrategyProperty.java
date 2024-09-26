package swiss.dasch.plugins.folderauthorizeprojectstrategy;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.authorizeproject.AuthorizeProjectProperty;
import org.jenkinsci.plugins.authorizeproject.AuthorizeProjectStrategy;
import org.jenkinsci.plugins.authorizeproject.ProjectQueueItemAuthenticator;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;

import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Descriptor;
import hudson.model.DescriptorVisibilityFilter;
import hudson.model.Items;
import net.sf.json.JSONObject;

public class FolderAuthorizeProjectStrategyProperty extends AbstractFolderProperty<AbstractFolder<?>> {

	private static final Logger LOGGER = Logger.getLogger(FolderAuthorizeProjectStrategyProperty.class.getName());

	@Nullable
	private AuthorizeProjectStrategy strategy;

	@DataBoundConstructor
	public FolderAuthorizeProjectStrategyProperty() {
	}

	@DataBoundSetter
	public void setStrategy(@Nullable AuthorizeProjectStrategy strategy) {
		this.strategy = strategy;
	}

	@Nullable
	public AuthorizeProjectStrategy getStrategy() {
		return this.strategy;
	}

	public boolean getEnabled() {
		return this.strategy != null;
	}

	/**
	 * From {@link AuthorizeProjectProperty#getEnabledStrategy()}
	 */
	@Nullable
	public AuthorizeProjectStrategy getEnabledStrategy() {
		if (!this.getEnabled()) {
			return null;
		}

		AuthorizeProjectStrategy strategy = this.getStrategy();

		if (strategy == null) {
			return null;
		}

		if (DescriptorVisibilityFilter
				.apply(ProjectQueueItemAuthenticator.getConfigured(), Collections.singleton(strategy.getDescriptor()))
				.isEmpty()) {
			LOGGER.log(Level.WARNING, "{0} is configured but disabled in the global-security configuration.",
					strategy.getDescriptor().getDisplayName());
			return null;
		}

		return strategy;
	}

	/**
	 * From {@link AuthorizeProjectProperty#setStrategyCritical()}
	 */
	@Initializer(after = InitMilestone.PLUGINS_STARTED)
	public static void setStrategyCritical() {
		Items.XSTREAM2.addCriticalField(FolderAuthorizeProjectStrategyProperty.class, "strategy");
	}

	@Extension
	@Symbol("authorizeProjectStrategy")
	public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

		@DataBoundConstructor
		public DescriptorImpl() {
		}

		@Override
		public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractFolder> folderType) {
			return ProjectQueueItemAuthenticator.isConfigured();
		}

		@Override
		public AbstractFolderProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			if (!formData.getBoolean("enabled")) {
				formData.clear();
			}
			return super.newInstance(req, formData);
		}

		/**
		 * From
		 * {@link AuthorizeProjectProperty.DescriptorImpl#getEnabledAuthorizeProjectStrategyDescriptorList()}
		 */
		public List<Descriptor<AuthorizeProjectStrategy>> getEnabledAuthorizeProjectStrategyDescriptorList() {
			ProjectQueueItemAuthenticator authenticator = ProjectQueueItemAuthenticator.getConfigured();
			if (authenticator == null) {
				return Collections.emptyList();
			}
			return DescriptorVisibilityFilter.apply(authenticator, AuthorizeProjectStrategy.all());
		}

		@Override
		public String getDisplayName() {
			return Messages.FolderAuthorizeProjectStrategyProperty_DisplayName();
		}

	}

}
