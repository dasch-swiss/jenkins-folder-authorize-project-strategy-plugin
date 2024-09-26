package swiss.dasch.plugins.folderauthorizeprojectstrategy;

import org.acegisecurity.Authentication;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.authorizeproject.AuthorizeProjectStrategy;
import org.jenkinsci.plugins.authorizeproject.AuthorizeProjectStrategyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;

import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.Queue;
import jenkins.model.Jenkins;

@SuppressWarnings("deprecation")
public class FolderAuthorizeProjectStrategy extends AuthorizeProjectStrategy {

	@DataBoundConstructor
	public FolderAuthorizeProjectStrategy() {
	}

	@Override
	public Authentication authenticate(Job<?, ?> job, Queue.Item item) {
		ItemGroup<?> parent = job.getParent();

		while (parent instanceof AbstractFolder) {
			AbstractFolder<?> folder = (AbstractFolder<?>) parent;

			FolderAuthorizeProjectStrategyProperty property = folder.getProperties()
					.get(FolderAuthorizeProjectStrategyProperty.class);

			if (property != null) {
				AuthorizeProjectStrategy strategy = property.getEnabledStrategy();

				if (strategy != null && strategy instanceof FolderAuthorizeProjectStrategy == false) {
					return strategy.authenticate(job, item);
				}
			}

			parent = folder.getParent();
		}

		return Jenkins.ANONYMOUS;
	}

	@Symbol("folderAuthorizationStrategy")
	@Extension
	public static class DescriptorImpl extends AuthorizeProjectStrategyDescriptor {

		@Override
		public String getDisplayName() {
			return Messages.FolderAuthorizeProjectStrategy_DisplayName();
		}

	}

}
