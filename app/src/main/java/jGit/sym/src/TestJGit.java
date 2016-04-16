package jGit.sym.src;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class TestJGit {

    private String localPath, remotePath;
    private Repository localRepo;
    private Git git;

    
    public TestJGit(Context context) throws IOException {
        localPath = context.getFilesDir().getPath();
        remotePath = "https://github.com/knadigatla/test-git.git";
        localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo);

    }

    
    public void testCreate() throws IOException {
        Repository newRepo = new FileRepository(localPath + ".git");
        newRepo.create();
    }

    
    public void testClone() throws IOException, GitAPIException {
        Git.cloneRepository().setURI(remotePath)
                .setDirectory(new File(localPath)).call();
    }

    
    public void testAdd() throws IOException, GitAPIException {
        git.add().addFilepattern("myfile").call();
    }

    
    public void testCommit() throws IOException, GitAPIException,
            JGitInternalException {
        git.commit().setMessage("Added myfile").call();
    }

    
    public void testPush() throws IOException, JGitInternalException,
            GitAPIException {
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("userid", "password");

        git.push().setCredentialsProvider(cp).call();
    }

    
    public void testTrackMaster() throws IOException, JGitInternalException,
            GitAPIException {
        git.branchCreate().setName("master")
                .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
                .setStartPoint("origin/master").setForce(true).call();
    }

    
    public void testPull() throws IOException, GitAPIException {
        git.pull().call();
    }
}