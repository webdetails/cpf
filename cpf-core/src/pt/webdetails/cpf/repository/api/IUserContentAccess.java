package pt.webdetails.cpf.repository.api;


/**
 * For user interaction with the repository.
 * Should always check permissions.<br>
 * @see {@link IReadAccess} {@link IRWAccess} {@link IACAccess}
 */
public interface IUserContentAccess extends IRWAccess, IACAccess {

}
