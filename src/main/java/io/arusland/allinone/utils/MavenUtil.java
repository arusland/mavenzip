package io.arusland.allinone.utils;

import com.jcabi.aether.Aether;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import io.arusland.allinone.Message;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Created by ruslan on 16.04.2016.
 */
public class MavenUtil {
    private final static Logger log = Logger.getLogger(MavenUtil.class.getName());

    public static void goOffline(File pomFile, File repositoryDir, List<Message> messages) {
        Collection<RemoteRepository> remotes = Arrays.asList(
                new RemoteRepository(
                        "maven-central",
                        "default",
                        "http://repo1.maven.org/maven2/"
                )
        );
        try {
            List<DefaultArtifact> artifacts = getArtifacts(pomFile);

            log.info("Artifacts: " + artifacts);

            for (DefaultArtifact art : artifacts) {
                log.info("Resolving dependency: " + art);
                new Aether(remotes, repositoryDir).resolve(art, "runtime");
                messages.add(new Message("Resolved dependency: " + art.toString(), "info"));
            }
        } catch (DependencyResolutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<DefaultArtifact> getArtifacts(File pomFile) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(pomFile));
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(in);
            MavenProject project = new MavenProject(model);
            List<DefaultArtifact> result = new ArrayList<DefaultArtifact>();

            for (Dependency dependency : project.getDependencies()) {
                result.add(new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                        "", "jar", dependency.getVersion()));
            }

            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
