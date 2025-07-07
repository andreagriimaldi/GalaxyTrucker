package it.polimi.ingsw.global;

import it.polimi.ingsw.global.ClientSession;

import java.util.*;

import it.polimi.ingsw.exceptions.UsernameAlreadyExistsException;
import it.polimi.ingsw.exceptions.ActiveSessionAlreadyExistsException;
import it.polimi.ingsw.exceptions.InvalidPasswordException;
import it.polimi.ingsw.exceptions.UsernameNotFoundException;

public class RegistrationManager {

    GlobalManager globalManager;


    /** USERNAMES **/
    List<String> registeredUsernames;

    /** FROM USERNAME TO PASSWORD **/
    Map<String, String> passwordHashes;


    public RegistrationManager(GlobalManager globalManager) {
        this.globalManager = globalManager;

        this.registeredUsernames = new ArrayList<String>();
        this.passwordHashes = new HashMap<String, String>();
    }

    /**
     * adds a new entry of username + password hash to the registered users stored in registration manager
     * @param username is the username of the player to register
     * @param passwordHash is the hash of the password of the player to register
     * @throws UsernameAlreadyExistsException if a username matches that in input
     */
    public void registerNewUser(String username, String passwordHash) throws UsernameAlreadyExistsException {

        System.out.println("registering new user " + username + " and password hash " + passwordHash);

        /** IF THE USERNAME PROVIDED BY THE USER IS FREE, THE SERVER PROCEEDS TO REGISTRATION **/
        if(!registeredUsernames.contains(username)) {
            System.out.println("new user credentials received by registration manager");

            registeredUsernames.add(username);
            passwordHashes.put(username, passwordHash);

        } else throw new UsernameAlreadyExistsException(username);
    }

    /**
     * checks 3 different conditions before allowing the player to log in
     * 1. the username doesn't exist (because no such username was ever registered)
     * 2. the username exists but the password hash doesn't match the password stored
     * in registration manager associated to that username
     * 3. there already exists a client session that was assigned the username of the
     * player that is now attempting to log in
     * @param clientSessionID is the ID of the client attempting to log in
     * @param username is the username to be assigned to the client session of the player attempting to log in
     * @param passwordHash is the hash of the password of the client session of the player attempting to log in
     * @throws UsernameNotFoundException if the username hasn't been registered
     * @throws InvalidPasswordException if the password is incorrect
     * @throws ActiveSessionAlreadyExistsException if there's an active client session to which the username is assigned
     */
    public void checkUserCredentials(String clientSessionID, String username, String passwordHash)
    throws UsernameNotFoundException, InvalidPasswordException, ActiveSessionAlreadyExistsException {

        System.out.println("requested login to registration manager");

        //LANCERA' 3 EXCEPTION DIVERSE, DI TIPO GENERICO, MA CON TESTO DIVERSO
        ClientSession clientSession = globalManager.getClientSession(clientSessionID);


        if(!registeredUsernames.contains(username)) { //1) lo username non esiste
            throw new UsernameNotFoundException(username);
        }
        else {
            if (!passwordHash.equals(passwordHashes.get(username))) { //2) lo username esiste ma la password non matcha
                throw new InvalidPasswordException();
            }
            else if (globalManager.isUserAlreadyAuthenticated(username)) { //3) lo username ha già una sessione attiva
                throw new ActiveSessionAlreadyExistsException(username);
            }
        }
    }
}