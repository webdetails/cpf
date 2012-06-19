/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.dom4j.Document;

/**
 *
 * @author pdpi
 */
public class RepositoryUtils {

    public final static String ENCODING = "utf-8";
    private static final Log logger = LogFactory.getLog(RepositoryUtils.class);

    public static void writeSolutionFile(String path, String fileName, String data) {
        try {
            ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
            solutionRepository.publish(PentahoSystem.getApplicationContext().getSolutionPath(""), path, fileName, data.getBytes(ENCODING), true);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void deleteSolutionFile(String path, String fileName) {
        try {
            String fullPath = path + "/" + fileName.replaceAll("\\\\", "/").replaceAll("/+", "/");

            ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
            solutionRepository.removeSolutionFile(fullPath);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void moveSolutionFile(String path, String fileName, String newPath, String newFileName) {
        /* Seems like there's no actual 'rename' functionality in the
         * solution repo, so we're stuck reading in the whole file,
         * writing it as a new file, then finally deleting the old one.
         */
        copySolutionFile(path, fileName, newPath, newFileName, true);
    }

    public static void copySolutionFile(String path, String fileName, String newPath, String newFileName) {
        copySolutionFile(path, fileName, newPath, newFileName, false);
    }

    private static void copySolutionFile(String path, String fileName, String newPath, String newFileName, boolean deleteOld) {
        try {

            String fullPath = path + "/" + fileName.replaceAll("\\\\", "/").replaceAll("/+", "/");
            ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
            byte[] data = solutionRepository.getResourceAsBytes(fullPath, true, 0);
            solutionRepository.publish(PentahoSystem.getApplicationContext().getSolutionPath(""), newPath, newFileName, data, true);
            if (deleteOld && !(newPath.equals(path) && newFileName.equals(fileName))) {
                solutionRepository.removeSolutionFile(fullPath);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static InputStream readSolutionFileAsStream(String path, String fileName) throws FileNotFoundException {


        String fullPath = path + "/" + fileName.replaceAll("\\\\", "/").replaceAll("/+", "/");
        return readSolutionFileAsStream(path);
    }

    
    public static Document readSolutionDocument(String path, String fileName) throws IOException {


        String fullPath = path + "/" + fileName.replaceAll("\\\\", "/").replaceAll("/+", "/");
        return readSolutionDocument(path);
    }
    public static Document readSolutionDocument(String path) throws IOException {

        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
        // Get the paths ot the necessary files: dependencies and the main script.
        return solutionRepository.getResourceAsDocument(path, 0);
    }    
    public static InputStream readSolutionFileAsStream(String path) throws FileNotFoundException {

        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
        // Get the paths ot the necessary files: dependencies and the main script.
        return solutionRepository.getResourceInputStream(path, false, 0);
    }
}