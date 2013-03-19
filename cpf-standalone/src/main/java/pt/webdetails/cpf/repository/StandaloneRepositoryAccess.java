package pt.webdetails.cpf.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.dom4j.Document;
import pt.webdetails.cpf.session.IUserSession;

import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.SaveFileStatus;

public class StandaloneRepositoryAccess implements IRepositoryAccess {
    
	@Override
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveFileStatus publishFile(String fileAndPath, String contents,
			boolean overwrite) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveFileStatus publishFile(String fileAndPath, byte[] data,
			boolean overwrite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveFileStatus publishFile(String solutionPath, String fileName,
			byte[] data, boolean overwrite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveFileStatus publishFile(String baseUrl, String path,
			String fileName, byte[] data, boolean overwrite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeFile(String solutionPath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeFileIfExists(String solutionPath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resourceExists(String solutionPath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createFolder(String solutionFolderPath) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWrite(String filePath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAccess(String filePath, FileAccess access) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getResourceInputStream(String filePath)
			throws FileNotFoundException {
		
		if(filePath==null) {
			filePath = "default-dashboard-template.html";
		}

		return loadFile(filePath);
	}

	@Override
	public InputStream getResourceInputStream(String filePath,
			FileAccess fileAccess) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getResourceInputStream(String filePath,
			FileAccess fileAccess, boolean getLocalizedResource)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getResourceAsDocument(String solutionPath)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getResourceAsDocument(String solutionPath,
			FileAccess fileAccess) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceAsString(String solutionPath) throws IOException {
		
		InputStream is = loadFile(solutionPath);
		
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "utf-8");
		return writer.toString();

	}

	@Override
	public String getResourceAsString(String solutionPath, FileAccess fileAccess)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveFileStatus copySolutionFile(String fromFilePath,
			String toFilePath) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
    private InputStream loadFile(String file) {

        try {
            FileSystemManager fsManager = VFS.getManager();
            FileObject content = fsManager.resolveFile("res:repository/" + file);
            
            FileContent fc = content.getContent(); 
            return fc.getInputStream(); 

        } catch (FileSystemException e) {
			e.printStackTrace();
		}
		return null;

    }

    @Override
    public String getSolutionPath(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setUserSession(IUserSession userSession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IRepositoryFile getRepositoryFile(String path, FileAccess fileAccess) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
