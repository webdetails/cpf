package pt.webdetails.cpf.repository;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.FileUtil;

public class VfsRepositoryFile implements IRepositoryFile {

	protected static final Log log = LogFactory.getLog(VfsRepositoryFile.class);
	
	private FileObject repo;
	private FileObject file;

	public VfsRepositoryFile(FileObject repo, FileObject file) {
		this.repo = repo;
		this.file = file;
	}
	
	@Override
	public boolean isDirectory() {
		try {
			return FileType.FOLDER.equals(file.getType());
		} catch (FileSystemException e) {
			throw new RuntimeException("Error accessing repository file: " + file, e);
		}
	}

	@Override
	public String getFileName() {
		try {
			String filename = file.getName().getBaseName();
			return filename;
		} catch (Exception e) {
			throw new RuntimeException("Error accessing repository file: " + file, e);
		}
	}

	@Override
	public String getSolutionPath() {
		try {
			String relativePath = repo.getName().getRelativeName(file.getName());
			return relativePath;
		} catch (Exception e) {
			throw new RuntimeException("Error accessing repository file: " + file, e);
		}
	}

	@Override
	public String getSolution() {
		throw new UnsupportedOperationException("getSolution is deprecated, not supported!");
	}

	@Override
	public String getFullPath() {
		try {
			String path = repo.getName().getPath();
			return path;
		} catch (Exception e) {
			throw new RuntimeException("Error accessing repository file: " + file, e);
		}
	}

	@Override
	public IRepositoryFile[] listFiles() {
		try {
			if (isDirectory()) {
				FileObject[] children = file.getChildren();
				if (children != null) {
					IRepositoryFile[] files = new IRepositoryFile[children.length];
					for(int i = 0; i < children.length; i++) {
						files[i] = new VfsRepositoryFile(repo, children[i]);
					}
					return files;
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Error accessing repository file: " + file, e);
		}

		return new IRepositoryFile[0];
	}

	@Override
	public IRepositoryFile[] listFiles(IRepositoryFileFilter iff) {
		List<IRepositoryFile> fileList = new ArrayList<IRepositoryFile>();
		IRepositoryFile[] files = listFiles();
		if (iff == null) {
			return files;
		}
		
		if (files != null ) {
			for (IRepositoryFile f : files) {
				if (iff.accept(f)) {
					fileList.add(f);
				}
			}
			return fileList.toArray(new IRepositoryFile[fileList.size()]);
		}
		return new IRepositoryFile[0];
	}

	@Override
	public boolean isRoot() {
		return (repo.getName().equals(file.getName()));
	}

	@Override
	public IRepositoryFile retrieveParent() {
		try {
			if (file.getParent().equals(repo) || getSolutionPath().startsWith("..")) {
				return null;
			}
			return new VfsRepositoryFile(repo, file.getParent());
		} catch (Exception e) {
			throw new RuntimeException("Error accessing retrieveParent: " + file, e);
		}
	}

	@Override
	public byte[] getData() {
		try {
			return FileUtil.getContent(file);
		} catch (Exception e) {
			throw new RuntimeException("Error reading file: " + file, e);
		}
	}

	@Override
	public boolean exists() {
		try {
			return file.exists();
		} catch (Exception e) {
			throw new RuntimeException("Error accessing exists: " + file, e);
		}
	}

	@Override
	public long getLastModified() {
		try {
			return file.getContent().getLastModifiedTime();
		} catch (Exception e) {
			throw new RuntimeException("Error accessing getLastModified: " + file, e);
		}	
	}

	@Override
	public String getExtension() {
		try {
			return file.getName().getExtension();
		} catch (Exception e) {
			throw new RuntimeException("Error accessing getExtension: " + file, e);
		}
	}

}
