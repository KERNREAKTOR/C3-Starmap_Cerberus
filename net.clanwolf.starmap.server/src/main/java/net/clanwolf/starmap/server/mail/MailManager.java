/* ---------------------------------------------------------------- |
 * W-7 Research Group / C3                                          |
 * ---------------------------------------------------------------- |
 *                                                                  |
 *          W-7 Facility / Research, Software Development           |
 *                    Tranquil (Mobile Division)                    |
 * __        __  _____   ____                               _       |
 * \ \      / / |___  | |  _ \ ___  ___  ___  __ _ _ __ ___| |__    |
 *  \ \ /\ / /____ / /  | |_) / _ \/ __|/ _ \/ _` | '__/ __| '_ \   |
 *   \ V  V /_____/ /   |  _ <  __/\__ \  __/ (_| | | | (__| | | |  |
 *    \_/\_/     /_/    |_| \_\___||___/\___|\__,_|_|  \___|_| |_|  |
 *                                                                  |
 *  W-7 is the production facility of Clan Wolf. The home base is   |
 *  Tranquil, but there are several mobile departments. In the      |
 *  development department there is a small group of developers and |
 *  designers busy to field new software products for battlefield   |
 *  commanders as well as for strategic dimensions of the clans     |
 *  operations. The department is led by a experienced StarColonel  |
 *  who fell out of active duty due to a wound he suffered during   |
 *  the battle on Tukkayid. His name and dossier are classified,    |
 *  get in contact through regular chain of command.                |
 *                                                                  |
 * ---------------------------------------------------------------- |
 *    ____ _____                                                    |
 *   / ___|___ /                   Communicate - Command - Control  |
 *  | |     |_ \                   MkIII "Damien"                   |
 *  | |___ ___) |                                                   |
 *   \____|____/                                                    |
 *                                                                  |
 *  One of the products used to control the production and the      |
 *  transport of frontline troops is C3. C3 stands for              |
 *  "Communication - Command - Control".                            |
 *  Because there is a field based system to control the            |
 *  communication and data transfer of Mechs, this system is often  |
 *  refered to as "Big C3", however, the official name is           |
 *  "W-7 C3 / MkIII 'Damien'".                                      |
 *                                                                  |
 *  Licensing through W-7 Facility Central Office, Tranquil.        |
 *                                                                  |
 * ---------------------------------------------------------------- |
 *                                                                  |
 *  Info        : http://www.clanwolf.net                           |
 *  Forum       : http://www.clanwolf.net                           |
 *  Web         : http://c3.clanwolf.net                            |
 *  GitHub      : https://github.com/ClanWolf/C3-Java_Client        |
 *                                                                  |
 *  IRC         : starmap.clanwolf.net @ Quakenet                        |
 *                                                                  |
 *  Devs        : - Christian (Meldric)                    [active] |
 *                - Werner (Undertaker)                    [active] |
 *                - Thomas (xfirestorm)                    [active] |
 *                - Domenico (Nonnex)                     [retired] |
 *                - Dirk (kotzbroken2)                    [retired] |
 *                                                                  |
 *                  (see Wolfnet for up-to-date information)        |
 *                                                                  |
 * ---------------------------------------------------------------- |
 *                                                                  |
 *  C3 includes libraries and source code by various authors,       |
 *  for credits and info, see README.                               |
 *                                                                  |
 * ---------------------------------------------------------------- |
 *                                                                  |
 * Copyright 2016 ClanWolf.net                                      |
 *                                                                  |
 * Licensed under the Apache License, Version 2.0 (the "License");  |
 * you may not use this file except in compliance with the License. |
 * You may obtain a copy of the License at                          |
 *                                                                  |
 * http://www.apache.org/licenses/LICENSE-2.0                       |
 *                                                                  |
 * Unless required by applicable law or agreed to in writing,       |
 * software distributed under the License is distributed on an "AS  |
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either  |
 * express or implied. See the License for the specific language    |
 * governing permissions and limitations under the License.         |
 *                                                                  |
 * ---------------------------------------------------------------- |
 */
package net.clanwolf.starmap.server.mail;

import net.clanwolf.starmap.logging.C3Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Handles sending of emails.
 * 
 * @author Christian
 *
 */
public class MailManager {
	private static String mailServer = null;
	private static String mailUser = null;
	private static String mailPassword = null;

	/**
	 * Empty constructor.
	 */
	public MailManager() {
		//
	}

	/**
	 * Sets the mail server to be used.
	 * 
	 * @param server
	 */
	public static void setMailCredentials(String server, String user, String password) {
		mailServer = server;
		mailUser = user;
		mailPassword = password;
	}

	/**
	 * Checks email adress for validity.
	 * 
	 * @param emailadress
	 * @return result of validity check
	 */
	private static boolean validEmailAdress(String emailadress) {
		boolean valid = false;
		try {
			InternetAddress internetAddress = new InternetAddress(emailadress);
			internetAddress.validate();
			valid = true;
		} catch (AddressException e) {
			e.printStackTrace();
			C3Logger.warning("Invalid Email Adress " + emailadress + " " + e.getMessage());
		}
		return valid;
	}

	/**
	 * Sends off a previouisly created Mail object.
	 * 
	 * @param mail
	 */
	private static boolean dispatch(Mail mail) {
		boolean result = false;

		javax.mail.Session session;
		Properties props;
		InternetAddress to;
		InternetAddress from;
		MimeMessage msg;

		Transport transport = null;

		try {
			props = new Properties();
			props.put("mail.smtp.host", mailServer);
			props.put("mail.smtp.connectiontimeout", "600000");
			props.put("mail.smtp.timeout", "600000");
			session = javax.mail.Session.getDefaultInstance(props, null);

			from = new InternetAddress(mail.sender());
			msg = new MimeMessage(session);
			msg.setFrom(from);
			msg.setHeader("X-Priority", Integer.toString(mail.priority()));
			if (mail.replyTo() != null && mail.replyTo().trim().length() > 0) {
				msg.setReplyTo(new Address[] { new InternetAddress(mail.replyTo()) });
			}
			String[] recs = mail.recipients().split(";");
			for (String rec : recs) {
				to = new InternetAddress(rec);
				msg.addRecipient(Message.RecipientType.TO, to);
			}
			msg.setSubject(mail.subject(), "UTF-8");

			if (!mail.html().isEmpty()) {
				msg.setContent(mail.html(), "text/html; charset=UTF-8");
			} else {
				msg.setText(mail.text(), "UTF-8");
			}
			msg.setSentDate(new Date());
			msg.saveChanges();

			transport = session.getTransport("smtp");
			transport.connect(mailUser, mailPassword);
			transport.sendMessage(msg, msg.getAllRecipients());

			result = true;
			C3Logger.info("Successfully sent email from " + mail.sender() + " to " + mail.recipients());
		} catch (Exception e) {
			C3Logger.error("Failed to send email " + e.getMessage(), e);
		} catch (Throwable t) {
			C3Logger.error("Failed to send email " + t.getMessage(), t);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					C3Logger.error("Failed to close Mail smtp Transport: ", e);
				}
			}
		}
		return result;
	}

	/**
	 * Sends a mail.
	 * 
	 * @param sender
	 * @param receivers
	 * @param subject
	 * @param content
	 * @param html
	 * @param breakOnError
	 * @return
	 */
	public static boolean sendMail(String sender, String[] receivers, String subject, String content, boolean html, boolean breakOnError) {
		if (mailServer == null || mailUser == null || mailPassword == null) {
			Properties mProperties = new Properties();
			try {
				mProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties"));
				String server = mProperties.getProperty("mail_server");
				String user = mProperties.getProperty("mail_user");
				String pw = mProperties.getProperty("mail_pw");
				MailManager.setMailCredentials(server, user, pw);
			} catch (IOException e) {
				C3Logger.error("Failed to read mail properties", e);
				return false;
			}
		}

		Mail mail = new Mail();
		if (MailManager.validEmailAdress(sender)) {
			mail.setSender(sender);
		} else {
			C3Logger.warning("Email Adress of Sender is not valid: " + sender);
			if (breakOnError) {
				throw new RuntimeException("Email Adress of Sender is not valid: " + sender);
			}
		}
		for (String receiver : receivers) {
			if (MailManager.validEmailAdress(receiver)) {
				mail.addRecipient(receiver);
			} else {
				C3Logger.warning("Email Adress of Receiver is not valid: " + receiver);
				if (breakOnError) {
					throw new RuntimeException("Email Adress of Receiver is not valid: " + receiver);
				}
			}
		}
		mail.setSubject(subject);

		if (content != null) {
			if (html) {
				mail.setHTML(content);
			} else {
				mail.setText(content);
			}
		}

		return MailManager.dispatch(mail);
	}
}