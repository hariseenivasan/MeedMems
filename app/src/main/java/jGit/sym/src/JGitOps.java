package jGit.sym.src;

import android.content.Context;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kiran on 4/6/16.
 */
public class JGitOps {

    final private static String USERNAME="shareyourmoments25";
    final private static String OAUTH_TOKEN="";
    final private static String GITHUB_URL="https://github.com";
    private static String LOCALPATH;



    public JGitOps(Context context){
        LOCALPATH = context.getFilesDir().getPath();

    }
    public JGitOps(String path) {
        LOCALPATH = path;
    }
    /*public static void main(String[] args) throws IOException {

        JGitOps jGitOps = new JGitOps();

        try {
            System.out.println(jGitOps.createRemoteRepo("test-kiran1234"));
//            jGitOps.clone("test-kiran1234");
            //make sure you need to call this settracker method after clone and before you do any other(push, pull add commit) ops
//            jGitOps.setTracker("test-kiran1234");
//            jGitOps.pull("test-kiran1234");
//            jGitOps.add("test-kiran1234");
//            jGitOps.commit("test-kiran1234","removing this readme.md");
//            jGitOps.push("test-kiran1234");
            jGitOps.deleteRemoteRepo("test-kiran1234");
        }catch(Exception e) {
            e.printStackTrace();
        }

    }*/

    public String createRemoteRepo(String repoName) throws IOException {
        Log.d("JGitOps","createRemoteRepo Method");
        GitHub gh = GitHub.connect(USERNAME,OAUTH_TOKEN);
        String name = gh.getMyself().getLogin()+"/"+repoName;
        try {
            GHRepository r = gh.getRepository(name);
            return r.toString();
        } catch (IOException e) {
            // No such repo found. No prob, we create it.
        }
        String descr = repoName + " created by "+USERNAME;
        GHRepository r = gh.createRepository(repoName).description(descr).create();
        return r.toString();
    }

    public boolean deleteRemoteRepo(String repoName) {
        Log.d("JGitOps","deleteRemoteRepo Method");

        try {
            GitHub gitHub = GitHub.connect(USERNAME,OAUTH_TOKEN);

            if (!gitHub.isCredentialValid()) {
                System.out.println("Invalid GitHub credentials");
            }
            GHRepository repo = null;
            if (USERNAME.compareToIgnoreCase(gitHub.getMyself().getLogin()) != 0) {
                GHOrganization org = gitHub.getOrganization(USERNAME);
                repo = org.getRepository(repoName);
            } else {
                repo = gitHub.getRepository(USERNAME + "/" + repoName);
            }
            repo.delete();

        } catch (IOException ex) {
            System.out.println("Problem authenticating with gihub-api when trying to remove a repository");
            return false;
        }
        return true;

    }

    public List<String> listRemoteRepos() throws IOException {
        Log.d("JGitOps","listRemoteRepos Method");

        Collection<GHRepository> lst = GitHub.connect(USERNAME,OAUTH_TOKEN).getUser(USERNAME).getRepositories().values();
        List<String> repoList = new ArrayList<String>(lst.size());
        for (GHRepository r : lst) {
            repoList.add(r.getName());
        }
        return repoList;
    }

    public void clone(String repoName) throws GitAPIException {
        Log.d("JGitOps","clone Method");

        Git.cloneRepository().setURI(GITHUB_URL+"/"+USERNAME+"/"+repoName+".git")
                .setDirectory(new File(LOCALPATH+"/"+repoName)).call();
    }

    public void pull(String repoName) throws IOException, GitAPIException {
        Log.d("JGitOps","pull Method");
        new Git(new FileRepository(LOCALPATH+"/"+repoName+"/"+".git")).pull().call();
    }

    public void push(String repoName) throws GitAPIException, IOException {
        Log.d("JGitOps","push Method");
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(OAUTH_TOKEN, "");
        new Git(new FileRepository(LOCALPATH+"/"+repoName+"/"+".git")).push().setCredentialsProvider(cp).call();

    }

    public void add(String repoName) throws IOException, GitAPIException {
        Log.d("JGitOps","add Method");
        new Git(new FileRepository(LOCALPATH+"/"+repoName+"/"+".git")).add().addFilepattern(".").call();
        Log.d("JGitOps","add methond:"+LOCALPATH+"/"+repoName+"/"+".git");
    }

    public void commit(String repoName, String message) throws IOException, GitAPIException {
        Log.d("JGitOps","commit Method");
        new Git(new FileRepository(LOCALPATH+"/"+repoName+"/"+".git")).commit().setMessage(message).call();
    }

    public void setTracker(String repoName) throws IOException, GitAPIException {
        Log.d("JGitOps","setTracker Method");
        new Git(new FileRepository(LOCALPATH+"/"+repoName+"/"+".git")).branchCreate().setName("master")
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                .setStartPoint("origin/master").setForce(true).call();    }
}
