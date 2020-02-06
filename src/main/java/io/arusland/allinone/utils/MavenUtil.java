package io.arusland.allinone.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.jcabi.aether.Aether;

import io.arusland.allinone.Message;

/**
 * Created by ruslan on 16.04.2016.
 */
public class MavenUtil {
	private final static Logger log = Logger.getLogger(MavenUtil.class.getName());

	public static void goOffline(File pomFile, File repositoryDir, List<Message> messages) {
		log.info("Resolving file " + pomFile);

		List<RemoteRepository> remotes = new ArrayList<RemoteRepository>();
		remotes.add(new RemoteRepository("maven-central", "default", "https://repo1.maven.org/maven2/"));

		try {
			MavenProject mavenProject = getMavenProject(pomFile);
			List<DefaultArtifact> artifacts = getArtifacts(mavenProject);
			List<RemoteRepository> repos = getRemoteRepositories(mavenProject, messages);

			remotes.addAll(repos);

			log.info("Artifacts: " + artifacts);
			log.info("Repositories: " + remotes);

			for (DefaultArtifact art : artifacts) {
				log.info("Resolving dependency: " + art);
				new Aether(remotes, repositoryDir).resolve(art, "runtime");
				messages.add(new Message("Resolved dependency: " + art.toString(), "info"));
			}
		} catch (DependencyResolutionException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (XmlPullParserException e) {
			throw new RuntimeException(e);
		}
	}

	public static void goOffline2(File pomFile, File repositoryDir, List<Message> messages) {
		log.info("Resolving file " + pomFile);


		try {

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c", String.format("mvn dependency:go-offline -f %s -Dmaven.repo.local=%s",
                    pomFile.getAbsolutePath(), repositoryDir.getAbsolutePath()));
            processBuilder.command("sh", "-c", String.format("mvn dependency:sources -f %s -Dmaven.repo.local=%s",
                    pomFile.getAbsolutePath(), repositoryDir.getAbsolutePath()));

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));


            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
            } else {
                //abnormal...
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static List<RemoteRepository> getRemoteRepositories(MavenProject mavenProject, List<Message> messages) {
		List<RemoteRepository> repos = new ArrayList<RemoteRepository>();

		for (Repository repo : mavenProject.getRepositories()) {
			repos.add(new RemoteRepository(repo.getId(), "default", repo.getUrl()));
		}

		if (mavenProject.getRemoteProjectRepositories() != null) {
			for (org.eclipse.aether.repository.RemoteRepository repo : mavenProject.getRemoteProjectRepositories()) {
				repos.add(new RemoteRepository(repo.getId(), "default", repo.getUrl()));
			}
		}

		if (mavenProject.getRemotePluginRepositories() != null) {
			for (org.eclipse.aether.repository.RemoteRepository repo : mavenProject.getRemotePluginRepositories()) {
				repos.add(new RemoteRepository(repo.getId(), "default", repo.getUrl()));
			}
		}

		if (mavenProject.getRemoteArtifactRepositories() != null) {
			for (ArtifactRepository repo : mavenProject.getRemoteArtifactRepositories()) {
				repos.add(new RemoteRepository(repo.getId(), "default", repo.getUrl()));
			}
		}
		
		for (RemoteRepository repo : repos) {
			messages.add(new Message("Additional repository: " + repo, "info"));
		}

		return repos;
	}

	public static List<DefaultArtifact> getArtifacts(MavenProject project) {
		try {
			List<DefaultArtifact> result = new ArrayList<DefaultArtifact>();

			for (Dependency dependency : project.getDependencies()) {
				result.add(new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), "", "jar",
						dependency.getVersion()));
			}

			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static MavenProject getMavenProject(File pomFile)
			throws FileNotFoundException, IOException, XmlPullParserException {
		BufferedReader in = new BufferedReader(new FileReader(pomFile));
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(in);
		MavenProject project = new MavenProject(model);
		return project;
	}
}
