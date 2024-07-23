package com.redxun.core.mail;

import com.redxun.core.mail.api.AttacheHandler;
import com.redxun.core.mail.model.Mail;
import com.redxun.core.mail.model.MailAddress;
import com.redxun.core.mail.model.MailAttachment;
import com.redxun.core.mail.model.MailConfig;
import com.redxun.core.util.StringUtil;
import com.sun.mail.imap.IMAPMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.security.Security;
import java.util.*;
import java.util.Map.Entry;

/**
 * <pre> 
 * 描述：邮件处理类，实现邮箱的接收、发送、测试连接功能
 * 作者：redxun
 * </pre>
 */
public class MailTools {
	private static final Logger logger = LoggerFactory.getLogger(MailTools.class);
	/**
	 * 邮件设置实体类
	 */
	private MailConfig mailConfig;
	/**
	 * 附件处理接口
	 */
	private AttacheHandler handler;

	private static final String CHARSET = "utf-8";

	/**
	 * 构造函数
	 * @param mailSetting	邮件设置实体类
	 *
	 * <pre>
	 * 接收示例：
	 * MailSetting setting = getMailSettingEntity();
	 * MailUtil util = new MailUtil(setting);
	 * List&lt;Mail&gt; list = util.receive(new AttacheHandler(){……});
	 *
	 * 发送示例：
	 * MailSetting setting = getMailSettingEntity();
	 * MailUtil util = new MailUtil(setting);
	 * Mail mail = getMailEntity();
	 * util.send(mail);
	 *
	 * 测试连接示例：
	 * MailSetting setting = getMailSettingEntity();
	 * MailUtil util = new MailUtil(setting);
	 * util.connectSmtpAndReceiver();
	 * </pre>
	 * @see AttacheHandler
	 * @see	MailConfig
	 * @see Mail
	 */
	public MailTools(MailConfig mailSetting) {
		this.mailConfig = mailSetting;
	}

	/**
	 * 测试发送邮件服务器和接收邮件服务器连接情况
	 * @throws MessagingException
	 */
	public void connectSmtpAndReceiver() throws MessagingException {
		connectSmtp();
		connectReciever();
	}

	/**
	 * 测试发送邮件服务器连接情况
	 *
	 * @throws MessagingException
	 */
	public void connectSmtp() throws MessagingException {
		// 取得通道session
		Session session = getMailSession(MailConfig.SMTP_PROTOCAL);
		// 创建smtp连接
		Transport transport = null;
		try {
			transport = session.getTransport(MailConfig.SMTP_PROTOCAL);
			transport.connect(mailConfig.getSendHost(),
					mailConfig.getMailAddress(), mailConfig.getPassword());
		} catch(MessagingException e){
			e.printStackTrace();
			throw e;
		} finally {
			transport.close();
		}
	}

	/**
	 * 测试接收邮件服务器连接情况
	 *
	 * @throws MessagingException
	 */
	public void connectReciever() throws MessagingException {
		Session session = getMailSession(mailConfig.getProtocal());
		// 创建连接
		Store store = null ;
		URLName urln = new URLName(mailConfig.getProtocal(),mailConfig.getReceiveHost(),Integer.parseInt(mailConfig.getReceivePort()),
				null,mailConfig.getMailAddress(),mailConfig.getPassword());
		try {
			store = session.getStore(urln);
			store.connect();
		} catch(MessagingException e){
			e.printStackTrace();
			throw e;
		} finally {
			store.close();
		}
	}

	/**
	 * 同步邮件
	 * @param messageId
	 * @param flag
	 * @throws Exception
	 */
	public void synchronization(String messageId,Flag flag) throws Exception{
		if(StringUtil.isEmpty(messageId)) return;
		Store connectedStore = getConnectedStore();
		Folder folder = getFolder(connectedStore);
		try {
			// 获得收件箱的邮件列表
	        Message[] messages = folder.getMessages();

	        // 解析邮件
	        for (Message message : messages) {
	        	if(message instanceof IMAPMessage) {
		            IMAPMessage msg = (IMAPMessage) message;
		            if(messageId.equals(msg.getMessageID())) {
			            // 第二个参数如果设置为true，则将修改反馈给服务器。false则不反馈给服务器
			            msg.setFlag(flag, this.mailConfig.getIsSynchronization());   //设置标志
			            break;
		            }
	        	}
	        }
		} catch (MessagingException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			close(folder, connectedStore);
		}
	}

	/**
	 * 发送邮件
	 *
	 * @param mail
	 *            邮件信息实体
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @see	Mail
	 */
	public void send(Mail mail) throws UnsupportedEncodingException, MessagingException {
		Session session = getMailSession(MailConfig.SMTP_PROTOCAL);

		MimeMessage message = new MimeMessage(session);
		addAddressInfo(mail, message);

		BodyPart contentPart = new MimeBodyPart();// 内容
		Multipart multipart = new MimeMultipart();
		contentPart.setHeader("Content-Transfer-Encoding", "base64");
		// 邮件正文(第二部分邮件的内容及附件)


		contentPart.setContent(mail.getContent(), "text/html;charset=utf-8");
		message.setSubject(mail.getSubject(), MailTools.CHARSET);
		message.setText(MailTools.CHARSET, MailTools.CHARSET);
		message.setSentDate(new Date());
		multipart.addBodyPart(contentPart);// 邮件正文
		message.setContent(multipart);
		// 添加附件
		for (MailAttachment attachment : mail.getMailAttachments()) {
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = null;
			String filePath = attachment.getFilePath();
			if(filePath==null || "".equals(filePath)){
				source = new ByteArrayDataSource(attachment.getFileBlob(), "application/octet-stream");
			}else {
				source = new FileDataSource(new File(filePath));
			}
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(MimeUtility.encodeWord(
					attachment.getFileName(), MailTools.CHARSET, "Q"));
			multipart.addBodyPart(messageBodyPart);
		}
		message.setContent(multipart);
		message.saveChanges();
		Transport transport = session.getTransport(MailConfig.SMTP_PROTOCAL);
		transport.connect(mailConfig.getSendHost(),
				mailConfig.getMailAddress(), mailConfig.getPassword());
		transport.sendMessage(message, message.getAllRecipients());
	}

	/**
	 * 接收邮件
	 * @param handler	附件处理类
	 * @return 邮件信息实体列表
	 * @throws Exception
	 * @see	AttacheHandler
	 */
	public List<Mail> receive(AttacheHandler handler) throws Exception {
		return receive(handler, "");
	}

	/**
	 * 接收邮件
	 * @param handler	附件处理类
	 * @param lastHandleMessageId	最近一次处理后保存的邮件messageId，若为空，则将获取所有邮件；若不为空，则只会获在该邮件之后接收到的邮件
	 * @return 邮件信息实体列表
	 * @throws Exception
	 * @see	AttacheHandler
	 */
	public List<Mail> receive(AttacheHandler handler, String lastHandleMessageId) throws Exception {
		this.handler = handler;
		Store connectedStore = getConnectedStore();
		Folder folder = getFolder(connectedStore);
		try {
			return getMessages(folder, lastHandleMessageId);
		} catch (MessagingException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			close(folder, connectedStore);
		}
	}

	/**
	 * 通过messageID，下载邮件
	 * @param handler	附件处理类
	 * @param messageID	所要下载的邮件messageID
	 * @return
	 * @throws Exception
	 */
	public Mail getByMessageID(AttacheHandler handler, String messageID) throws Exception{
		this.handler = handler;
		Store connectedStore = getConnectedStore();
		Folder folder = getFolder(connectedStore);
		SearchTerm searchTerm = new MessageIDTerm(messageID);
		Message messages[] = folder.search(searchTerm);
		if(messages==null || messages.length==0) return null;
		List<Mail> mailList = new ArrayList<Mail>();
		buildMailList(messageID, (MimeMessage)messages[0], mailList);
		return mailList.get(0);
	}

	private Store getConnectedStore() throws MessagingException{
		Session session = getMailSession(mailConfig.getProtocal());
		URLName urln = new URLName(mailConfig.getProtocal(),mailConfig.getReceiveHost(),Integer.parseInt(mailConfig.getReceivePort()),
				null,mailConfig.getMailAddress(),mailConfig.getPassword());
		// 创建连接
		Store store = session.getStore(urln);
		store.connect();
		return store;
	}

	private Folder getFolder(Store store) throws MessagingException{
		Folder folder = store.getFolder("INBOX");
		if(mailConfig.getIsSynchronization()){// 需要同步远程邮件，则以读写方式打开
			folder.open(Folder.READ_WRITE);
		}else {
			folder.open(Folder.READ_ONLY);
		}
		return folder;
	}

	/**
	 * 若lastHandleMessageId为空，则返回所有邮件数组：Message[]<br>
	 * 若lastHandleMessageId不为空，则会将lastHandleMessageId邮件之后的邮件构造传入的mailList列表，并返回Null
	 * @param folder
	 * @param lastHandleMessageId
	 * @param
	 * @return
	 * @throws Exception
	 */
	private List<Mail> getMessages(Folder folder, String lastHandleMessageId) throws Exception{
		Message messages[] = new Message[0];
		FetchProfile profile = new FetchProfile();// 感兴趣的信息
		profile.add(UIDFolder.FetchProfileItem.UID);// 邮件标识id
		folder.fetch(messages, profile);
		int total = folder.getMessageCount();
		List<Mail> mailList = new ArrayList<Mail>();
		boolean isLastHandleMessageIdNotEmpty = (lastHandleMessageId != null && !"".equals(lastHandleMessageId.trim()));
		mailList = getMessages(folder, lastHandleMessageId, mailList, isLastHandleMessageIdNotEmpty, total);

		return mailList;
	}

	/**
	 * 若lastHandleMessageId为空，则返回所有邮件数组：Message[]<br>
	 * 若lastHandleMessageId不为空，则会将lastHandleMessageId邮件之后的邮件构造传入的mailList列表，并返回Null
	 * @param folder
	 * @param lastHandleMessageId
	 * @param mailList
	 * @param isLastHandleMessageIdNotEmpty
	 * @param endIndex	需要获取的最后一封邮件的索引号
	 * @return
	 * @throws Exception
	 */
	private List<Mail> getMessages(Folder folder, String lastHandleMessageId, List<Mail> mailList, boolean isLastHandleMessageIdNotEmpty, int endIndex) throws Exception{
		MimeMessage msg = null;
		Message[] messages = folder.getMessages();
		try{
			for (int i = messages.length-1; i >= 0; i--){
				msg = (MimeMessage) messages[i];
				String messageId = msg.getMessageID();
				if(isLastHandleMessageIdNotEmpty && messageId.equals(lastHandleMessageId)) break;
				buildMailList(messageId, msg, mailList);
			}
		}catch(FolderClosedException closeException){// 如果同步的过程中，邮箱连接被关闭了，则重新打开一个连接
			folder = getFolder(folder.getStore());
			getMessages(folder, lastHandleMessageId, mailList, isLastHandleMessageIdNotEmpty, endIndex-mailList.size());
		}

		// 反转list列表，因上述遍历是从最新的邮件开始插入list中，现需要将list进行反转，将最旧的邮件放到list的开头
		// 与folder.getMessages()获取邮件的顺序保持一致
		Collections.reverse(mailList);
		return mailList;
	}

	/**
	 * 根据messageId判断是否下载邮件，如果下载，将解析message之后，存放到list对象中
	 * @param messageId
	 * @param message
	 * @param list
	 * @throws Exception
	 * @see Message
	 */
	private void buildMailList(String messageId, MimeMessage message, List<Mail> list) throws Exception{
		if(handler.isDownlad(messageId)==null || !handler.isDownlad(messageId)) return ;
		Mail mail = getMail(message);
		mail.setMessageId(messageId);
		list.add(mail);
		if(mailConfig.getIsDeleteRemote() && mailConfig.getIsHandleAttach()){
			message.setFlag(Flags.Flag.DELETED, true);//设置已删除状态为true
		}
	}

	/**
	 * 根据MimeMessage获得Mail实体
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 * @see	MimeMessage
	 */
	private Mail getMail(MimeMessage message) throws Exception {
		Mail mail = new Mail();
		Date sentDate = null;
		if (message.getSentDate() != null) {
			sentDate = message.getSentDate();
		} else {
			sentDate = new Date();
		}
		// 邮件发送时间
		mail.setSendDate(sentDate);
		//处理邮箱状态
		handlerMailStatus(message, mail);
		//MessaageId
		mail.setMessageId(message.getMessageID());
		String subject = message.getSubject();
		subject = handlerSubject(message, subject);//处理主题乱码
		mail.setSubject(subject);
		// 发件人
		MailAddress temp = getFrom(message);
		mail.setSenderAddress(temp.getAddress());
		mail.setSenderName(temp.getName());
		// 接受者
		temp = getMailAddress(Message.RecipientType.TO,message);
		mail.setReceiverAddresses(temp.getAddress());
		mail.setReceiverName(temp.getName());
		// 暗送者
		temp = getMailAddress(Message.RecipientType.BCC,message);
		mail.setBcCAddresses(temp.getAddress());
		mail.setBccName(temp.getName());
		// 抄送者
		temp = getMailAddress(Message.RecipientType.CC,message);
		mail.setCopyToAddresses(temp.getAddress());
		mail.setCopyToName(temp.getName());

		// 取得邮件内容
		StringBuffer bodytext = new StringBuffer();
		getMailContent(message, bodytext, mail);
		mail.setContent(bodytext.toString());

		return mail;
	}

	/**
	 * 获得发件人的地址和姓名
	 * @see	MimeMessage
	 */
	private MailAddress getFrom(MimeMessage mimeMessage)
			throws Exception {
		MailAddress mailAddress = new MailAddress();
		try {
			InternetAddress address[] = (InternetAddress[]) mimeMessage
					.getFrom();
			if (address == null || address.length == 0)
				return mailAddress ;
			mailAddress.setAddress(address[0].getAddress());
			mailAddress.setName(address[0].getPersonal());
		} catch (Exception ex) {
		}
		return mailAddress;
	}

	/**
	 * 根据RecipientType类型，获得邮件相应的收件人信息：邮箱地址,邮箱名称
	 *
	 * @param recipientType
	 * @param mimeMessage
	 * @return
	 * @throws Exception
	 * @see	RecipientType
	 * @see	MimeMessage
	 */
	private MailAddress getMailAddress(RecipientType recipientType, MimeMessage mimeMessage) throws Exception {
		MailAddress mailAddress = new MailAddress();
		InternetAddress[] address = (InternetAddress[]) mimeMessage.getRecipients(recipientType);
		if(address==null) return mailAddress;
		StringBuffer addresses = new StringBuffer("");
		StringBuffer name = new StringBuffer("");
		for (int i = 0; i < address.length; i++) {
			String email = address[i].getAddress();
			if(email==null) continue ;
			String personal = address[i].getPersonal();
			if(personal==null) personal = email;
			switch(i){
			case 0:
				addresses.append(MimeUtility.decodeText(email));
				name.append(MimeUtility.decodeText(personal));
				break ;
			default:
				addresses.append(",").append(MimeUtility.decodeText(email));
				name.append(",").append(MimeUtility.decodeText(personal));
			}
		}
		mailAddress.setAddress(addresses.toString());
		mailAddress.setName(name.toString());
		return mailAddress;
	}

	/**
	 * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中， 解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
	 * @param	message
	 * @param	bodyText
	 * @param	mail
	 * @see	Part
	 * @see	Mail
	 */
	private void getMailContent(Part message, StringBuffer bodyText, Mail mail) throws Exception {
		String contentType = message.getContentType();
		int nameindex = contentType.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		if (message.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) message.getContent();

			int count = multipart.getCount();
			Map<String, Part> partMap = new LinkedHashMap<String, Part>();

			boolean blnTxt = false;
			boolean blnHtml = false;
			for (int i = 0; i < count; i++) {
				Part tmpPart = multipart.getBodyPart(i);
				String partType = tmpPart.getContentType();
				if (tmpPart.isMimeType("text/plain")) {
					partMap.put("text/plain", tmpPart);
					blnTxt = true;
				} else if (tmpPart.isMimeType("text/html")) {
					partMap.put("text/html", tmpPart);
					blnHtml = true;
				} else {
					partMap.put(partType, tmpPart);
				}
			}
			if (blnTxt && blnHtml) {
				partMap.remove("text/plain");
			}
			Set<Entry<String, Part>> set = partMap.entrySet();
			for (Iterator<Entry<String, Part>> it = set.iterator(); it
					.hasNext();) {
				Part tempMessage = it.next().getValue();
				if ((tempMessage.isMimeType("text/plain") || tempMessage .isMimeType("text/html")) && !conname) {
					if(set.size()==1) {
						bodyText.append((String) tempMessage.getContent());
						if (mailConfig.getIsHandleAttach()) {
							mail.setContent(bodyText.toString());
							handler.handle(tempMessage, mail);
						} else {
							// 不处理附件下载，则只记录下附件的文件名
							String filename=MimeUtility.decodeText(tempMessage.getFileName());
							mail.getMailAttachments().add(new MailAttachment(filename, ""));
						}
					}
				}else if (tempMessage.isMimeType("application/octet-stream")
						 || tempMessage.isMimeType("image/*")
						 || tempMessage.isMimeType("application/*")) {
					if (mailConfig.getIsHandleAttach()) {
						mail.setContent(bodyText.toString());
						Multipart mp = (Multipart) ((BodyPart)tempMessage).getParent();
						for (int i = 0; i < mp.getCount(); i++) {
							BodyPart mpart = mp.getBodyPart(i);
							handlerAttachMent(mail,mpart);
						}
						handler.handle(tempMessage, mail);
					} else {
						// 不处理附件下载，则只记录下附件的文件名
						String filename=MimeUtility.decodeText(tempMessage.getFileName());
						mail.getMailAttachments().add(new MailAttachment(filename, ""));
					}
				}
			}

		} else if (message.isMimeType("message/rfc822")) {
			getMailContent((Part) message.getContent(), bodyText, mail);
		}
	}

	/**
	 * 根据传入的协议类型，返回Properties
	 * @param protocal	有IMAP、SMTP、POP3
	 * @return	Properties
	 */
	private Properties getProperty(String protocal) {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Properties props = new Properties();
		if (mailConfig.getSSL()) {
			props.setProperty("mail." + protocal + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		props.setProperty("mail." + protocal + ".socketFactory.fallback","false");

		if (MailConfig.SMTP_PROTOCAL.equals(protocal)) {
			String host = mailConfig.getSendHost();
			props.setProperty("mail.smtp.host", host);
			props.setProperty("mail.smtp.port", mailConfig.getSendPort());
			props.setProperty("mail.smtp.socketFactory.port", mailConfig.getSendPort());
			props.setProperty("mail.smtp.auth", String.valueOf(mailConfig.getValidate()));
			int gmail = host.indexOf("gmail");
			int live = host.indexOf("live");
			if (gmail != -1 || live != -1) {
				props.setProperty("mail.smtp.starttls.enable", "true");
			}
			if (!mailConfig.getSSL()) {
				props.setProperty("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
			}
		} else {
			props.setProperty("mail." + protocal + ".host", mailConfig.getReceiveHost());
			props.setProperty("mail." + protocal + ".port", mailConfig.getReceivePort());
			props.setProperty("mail." + protocal + ".socketFactory.port", mailConfig.getReceivePort());
			if (MailConfig.POP3_PROTOCAL.equals(protocal)) {
				props.setProperty("mail.smtp.starttls.enable", "true");
			} else {
				props.setProperty("mail.store.protocol", MailConfig.IMAP_PROTOCAL);
			}
		}
		return props;
	}

	/**
	 * 根据协议，获取邮箱连接session
	 * @param protocal	有IMAP、SMTP、POP3
	 * @return 邮箱连接session
	 */
	private Session getMailSession(String protocal) {
		// Get a Properties object
		Properties props = getProperty(protocal);
		// 如果不要对服务器的ssl证书进行受信任检查，测添加以下语句
		// mailProps.setProperty("mail.smtp.ssl.trust","*");
		Session mailSession = null ;
		if(MailConfig.IMAP_PROTOCAL.equals(protocal)){
			mailSession = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication goPasswordAuthentication() {
					return new PasswordAuthentication(mailConfig.getMailAddress(), mailConfig.getPassword());
				}
			});
		}else {
			mailSession = Session.getInstance(props, null);
		}
		return mailSession;
	}

	/**
	 * 添加发件人、收件人、抄送人、暗送人地址信息
	 *
	 * @param mail
	 * @param message
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 * @throws MessagingException
	 * @see	Message
	 * @see	Mail
	 */
	private void addAddressInfo(Mail mail, Message message) throws UnsupportedEncodingException, MessagingException {
		// 添加发件人
		InternetAddress senderAddress = toInternetAddress(mailConfig.getNickName(), mailConfig.getMailAddress());
		message.setFrom(senderAddress);
		// 收件人列表
		addAddressInfo(message, mail.getReceiverAddresses(), Message.RecipientType.TO);
		// 抄送人列表
		addAddressInfo(message, mail.getCopyToAddresses(), Message.RecipientType.CC);
		// 暗送人列表
		addAddressInfo(message, mail.getBcCAddresses(), Message.RecipientType.BCC);
	}

	/**
	 * 根据传入的带,号的address，添加地址信息
	 *
	 * @param
	 * @param message
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @see	Message
	 * @see	RecipientType
	 */
	private void addAddressInfo(Message message, String address, RecipientType recipientType) throws UnsupportedEncodingException, MessagingException {
		MailAddress mailAddress = new MailAddress();
		List<MailAddress> addressList = new ArrayList<MailAddress>();
		if (address != null && !"".equals(address)) {
			String[] addressArr = address.split(",");
			for (String id : addressArr) {
				mailAddress = new MailAddress();
				mailAddress.setAddress(id);
				mailAddress.setName(id);
				addressList.add(mailAddress);
			}
		}
		if (addressList == null || addressList.size() < 1) return ;
		InternetAddress addressArr[] = toInternetAddress(addressList);
		if (addressArr != null)
			message.addRecipients(recipientType, addressArr);
	}

	/**
	 * 将地地址址转化为 可输送的网络地址
	 * @param	name	显示名称
	 * @param	address	邮件地址
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 */
	private InternetAddress toInternetAddress(String name, String address)
			throws UnsupportedEncodingException, AddressException {
		if (name != null && !name.trim().equals("")) {
			return new InternetAddress(address, MimeUtility.encodeWord(name,
					MailTools.CHARSET, "Q"));
		}
		return new InternetAddress(address);
	}

	/**
	 * 将地地址址转化为 可输送的网络地址
	 * @param	emailAddress	MailAddress实体对象
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 * @see	MailAddress
	 */
	private InternetAddress toInternetAddress(MailAddress emailAddress)
			throws UnsupportedEncodingException, AddressException {
		return toInternetAddress(emailAddress.getName(),
				emailAddress.getAddress());
	}

	/**
	 * 将地地址址转化为 可输送的网络地址
	 * @param	list	MailAddress实体对象列表
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 * @see	MailAddress
	 */
	private InternetAddress[] toInternetAddress(List<MailAddress> list)
			throws UnsupportedEncodingException, AddressException {
		if (list == null)
			return null;
		InternetAddress address[] = new InternetAddress[list.size()];
		for (int i = 0; i < list.size(); i++) {
			address[i] = toInternetAddress(list.get(i));
		}
		return address;
	}

	/**
	 * 关闭邮箱连接，关闭时，根据MailSetting中设置的isDeleteRemote，决定是否删除远程邮件
	 * @param folder	java.mail.Folder
	 * @param store	javax.mail.Store
	 * @throws UnsupportedEncodingException
	 * @see	Folder
	 * @see Store
	 */
	private void close(Folder folder, Store store) {
		try {
			if (folder != null && folder.isOpen()) {
				//是否删除远程邮件
				folder.close(mailConfig.getIsDeleteRemote());
			}
			if (store != null && store.isConnected()) {
				store.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			folder = null;
			store = null;
		}
	}

	/**
	 * 处理"=?...?="的乱码内容
	 *
	 * @param subject
	 *            需要解码的字符串
	 * @return
	 * @throws Exception
	 */
	public static String handle(String subject) throws Exception {
		if (!(subject.contains("=?"))) { // 如果不含"=?"
			return subject;
		}
		subject = handleComplex(subject);
		StringBuffer sb = new StringBuffer(subject);
		String n = "";
		int temp = 0;
		char[] a;

		if (subject.contains("=?GB") || subject.contains("=?gb")) { // 处理GBK,GB2312,GB18030以及GB开头的编码的字符串
			if (subject.contains("=?GB")) {
				while (subject.indexOf("GB", temp) != -1) {
					a = subject.toCharArray();
					temp = subject.indexOf("GB", temp);
					temp = subject.indexOf("?", temp);
					if (a[temp + 1] >= 'a' && a[temp + 1] <= 'z') {
						a[temp + 1] = Character.toUpperCase(a[temp + 1]);
					}
					n = new String(a);
				}
			}
			if (subject.contains("=?gb")) {
				while (subject.indexOf("gb", temp) != -1) {
					a = subject.toCharArray();
					temp = subject.indexOf("gb", temp);
					temp = subject.indexOf("?", temp);
					if (a[temp + 1] >= 'a' && a[temp + 1] <= 'z') {
						a[temp + 1] = Character.toUpperCase(a[temp + 1]);
					}
					n = new String(a);
				}
			}
			return MimeUtility.decodeText(n);
		}

		if ((subject.indexOf("=?utf") != -1) || (subject.indexOf("=?UTF") != -1)) { // 处理UTF8的编码的字符串
			if (subject.contains("=?utf-8")) { // 处理utf-8编码的字符串
				while (subject.indexOf("utf-8", temp) != -1) {
					a = subject.toCharArray();
					temp = subject.indexOf("utf-8", temp);
					if (a[temp + 6] >= 'a' && a[temp + 6] <= 'z') {
						a[temp + 6] = Character.toUpperCase(a[temp + 6]);
					}
					n = new String(a);
					temp = temp + 5;
				}
			}
			if (subject.contains("=?utf8")) { // 处理utf8编码的字符串
				while (subject.indexOf("utf8", temp) != -1) {
					a = subject.toCharArray();
					temp = subject.indexOf("utf8", temp);
					if (a[temp + 5] >= 'a' && a[temp + 5] <= 'z') {
						a[temp + 5] = Character.toUpperCase(a[temp + 5]);
					}
					n = new String(a);
					temp = temp + 4;
				}
			}
			if (subject.contains("=?UTF8")) { // 处理UTF8编码的字符串
				while (subject.indexOf("UTF8", temp) != -1) {
					a = subject.toCharArray();
					temp = subject.indexOf("UTF8", temp);
					if (a[temp + 5] >= 'a' && a[temp + 5] <= 'z') {
						a[temp + 5] = Character.toUpperCase(a[temp + 5]);
					}
					n = new String(a);
					temp = temp + 4;
				}
			}
			if (subject.contains("=?UTF-8")) { // 处理UTF-8编码的字符串
				while (subject.indexOf("UTF-8", temp) != -1) {
					a = subject.toCharArray();
					temp = subject.indexOf("UTF-8", temp);
					if (a[temp + 6] >= 'a' && a[temp + 6] <= 'z') {
						a[temp + 6] = Character.toUpperCase(a[temp + 6]);
					}
					n = new String(a);
					temp = temp + 5;
				}
			}
			return MimeUtility.decodeText(n);
		}

		return MimeUtility.decodeText(sb.toString()); // 其他编码直接解码
	}

	/**
	 * 处理"=?"，"=?="错误串，并使其只能正常显示
	 *
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public static String handleComplex(String subject) throws Exception {
		int temp = 0;
		while (subject.indexOf("=?", temp) > -1) { // 去除加密字符串中的无用空格
			int startIndex = subject.indexOf("=?", temp);
			int endIndex = subject.indexOf("?=", startIndex + 2);
			String a = subject.substring(0, startIndex);
			String b = subject.substring(startIndex, endIndex).replaceAll("\\s*", "");
			String c = subject.substring(endIndex);
			subject = a + b + c;
			temp = endIndex;
		}
		temp = 0;

		if (subject.contains("=?=")) { // 处理加密字符串无用的"="
			StringBuffer s = new StringBuffer(subject);
			while (s.indexOf("=?=", temp) != -1) {
				if (s.charAt(s.indexOf("=?=", temp) - 1) != '=' || (s.charAt(s.indexOf("=?=", temp) - 1) != '='
						&& s.charAt(s.indexOf("=?=", temp) - 2) != '=')) {
					s.deleteCharAt(s.indexOf("=?=", temp));
					temp = s.indexOf("=?=", temp) + 3;
					continue;
				}
				temp = s.indexOf("=?=", temp) + 3;
			}
			if (!MimeUtility.decodeText(s.toString()).contains("=?")
					&& !MimeUtility.decodeText(s.toString()).endsWith("?="))
				subject = s.toString();
		}
		if (subject.indexOf("=?") <= 0)
			return subject;
		else {
			int i = 0;
			while (subject.indexOf("=?", i) > 0) { // 处理多个加密字符串连在一起 如?==? 加空格
				subject = stringInsert(subject, " =", subject.indexOf("=?", i));
				i = subject.indexOf("=?", i) + 2;
			}
			StringBuffer sb = new StringBuffer(subject);
			if (sb.charAt(sb.indexOf("=?") - 1) == ' ' && sb.charAt(sb.indexOf("=?") - 2) == ' ')
				sb.deleteCharAt(sb.indexOf("=?") - 1);
			subject = sb.toString();
			return subject;
		}
	}

	/**
	 * 在字符串某个位置插入字符
	 *
	 * @param a
	 *            原来字符串
	 * @param b
	 *            要插入的字符串
	 * @param t
	 *            插入位置
	 * @return
	 */
	public static String stringInsert(String a, String b, int t) {
		return a.substring(0, t) + b + a.substring(t + 1, a.length());
	}

	/**
	 * 判断乱码字符串是否解码
	 *
	 * @param message
	 *            外部邮件信息
	 * @return
	 * @throws Exception
	 */
	public static String trans(MimeMessage message) throws Exception {
		String subject = "";
		if (message.getSubject() != null)
			subject = message.getSubject();
		InternetAddress[] addresses;
		addresses = (InternetAddress[]) message.getFrom(); // 获取发件人
		String fromname = "";
		if (addresses != null && addresses.length > 0) {
			fromname = addresses[0].getPersonal(); // 获取发件人别名
			if (fromname == null)
				fromname = "";
		}
		addresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
		String toname = "";
		if (addresses != null && addresses.length > 0) {
			toname = addresses[0].getPersonal(); // 获取收件人别名
			if (toname == null)
				toname = "";
		}
		addresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
		String ccname = "";
		if (addresses != null && addresses.length > 0) {
			ccname = addresses[0].getPersonal(); // 获取抄送人别名
			if (ccname == null)
				ccname = "";
		}
		addresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.BCC);
		String bccname = "";
		if (addresses != null && addresses.length > 0) {
			bccname = addresses[0].getPersonal(); // 获取暗送人别名
			if (bccname == null)
				bccname = "";
		}
		if (StringUtils.isNotEmpty(subject) || StringUtils.isNotEmpty(fromname) || StringUtils.isNotEmpty(toname)
				|| StringUtils.isNotEmpty(ccname) || StringUtils.isNotEmpty(bccname)) { // 当主题或者发件人或者收件人或者抄送人或者暗送人的某一个不为空的时候
			String to = "";
			String cc = "";
			String bcc = "";
			if (message.getHeader("To") != null && message.getHeader("To").length > 0)
				to = message.getHeader("To")[0]; // 从邮件头部获取收件人
			if (message.getHeader("CC") != null && message.getHeader("CC").length > 0)
				cc = message.getHeader("CC")[0]; // 从邮件头部获取抄送人
			if (message.getHeader("BCC") != null && message.getHeader("BCC").length > 0)
				bcc = message.getHeader("BCC")[0]; // 从邮件头部获取暗送人
			if (StringUtils.isNotEmpty(to) && StringUtils.isNotEmpty(cc) && StringUtils.isNotEmpty(bcc)) { // 当收件人，抄送人，暗送人都不为空的时候
				String subject1 = "";
				if (message.getHeader("Subject") != null && message.getHeader("Subject").length > 0)
					subject1 = message.getHeader("Subject")[0]; // 从邮件头部获取主题

				if ((!subject.contains("=?") && subject1.startsWith("=?"))
						|| (!fromname.contains("=?") && message.getHeader("From")[0].startsWith("=?"))
						|| (!toname.contains("=?") && message.getHeader("To")[0].startsWith("=?"))
						|| (!ccname.contains("=?") && message.getHeader("CC")[0].startsWith("=?"))
						|| (!bccname.contains("=?") && message.getHeader("BCC")[0].startsWith("=?"))) // 当主题或者发件人或者收件人或者抄送人或者暗送人，当中的某一个主题或别名满足不含"=?"开头的字符串同时未解码的时候以"=?"开头的时候
					return "notrans";
				else
					return getCharset(message); // 获取字符集
			} else {
				String subject1 = "";
				if (message.getHeader("Subject") != null && message.getHeader("Subject").length > 0)
					subject1 = message.getHeader("Subject")[0]; // 从邮件头部获取主题
				if ((!subject.contains("=?") && subject1.startsWith("=?"))
						|| (!fromname.contains("=?") && message.getHeader("From")[0].startsWith("=?")))// 当主题或者发件人不包含"=?"以及从邮件头部获取的信息以"=?"开头
					return "notrans";
				else
					return getCharset(message); // 获取字符集
			}

		} else
			return "utf-8";
	}

	/**
	 * 从外部邮件头部获取邮件编码
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String getCharset(MimeMessage message) throws Exception { // 获取字符集
		String charset = "gb2312"; // 默认编码
		String s = message.getContentType(); // 从contenttype获取字符集
		if (s.indexOf("charset") != -1) {
			int index = s.indexOf("charset");
			int startIndex = index + 8;
			int endIndex = 0;
			if (s.indexOf("\"", startIndex + 1) > 0) // 如果charset后带双引号，则结束索引为下一个双引号
				endIndex = s.indexOf("\"", startIndex + 1);
			else
				endIndex = s.length();
			charset = s.substring(startIndex, endIndex);
			if (charset.contains("\\")) { // 如果字符串中有反斜杠，则从开始截取到反斜杠
				charset = charset.substring(0, charset.indexOf("\\"));
			}
			if (charset.contains("\"")) { // 如果字符串中有双引号
				StringBuffer sb2 = new StringBuffer(charset);
				sb2.deleteCharAt(0); // 删除最后一个字符
				charset = sb2.toString();
				if (charset.contains("/")) { // 如果包含"/"
					charset = "gb2312";
				}
			} else { // 没有双引号
				if (charset.contains("/")) { // 如果包含"/"
					charset = "gb2312";
				}
			}
			charset = charset.trim(); // 去除首位空白
			return charset;
		}
		s = message.getHeader("Subject")[0]; // 主题获取字符集
		if (s.contains("=?")) { // 从一个问号和第二个问号中获取主题字符集
			StringBuffer sb = new StringBuffer(s);
			int startIndex = sb.indexOf("?");
			int endIndex = sb.indexOf("?", startIndex + 1);
			charset = sb.substring(startIndex + 1, endIndex);
			return charset;
		}
		s = message.getHeader("From")[0]; // 发件人获取字符集
		if (s.contains("=?")) { // 从一个问号和第二个问号中获取发件人字符集
			StringBuffer sb = new StringBuffer(s);
			int startIndex = sb.indexOf("?");
			int endIndex = sb.indexOf("?", startIndex + 1);
			charset = sb.substring(startIndex + 1, endIndex);
			return charset;
		}

		if (message.getHeader("To") != null && message.getHeader("To").length > 0) // 收件人获取字符集
			s = message.getHeader("To")[0];
		else
			s = "";
		if (s.contains("=?")) { // 从一个问号和第二个问号中获取收件人字符集
			StringBuffer sb = new StringBuffer(s);
			int startIndex = sb.indexOf("?");
			int endIndex = sb.indexOf("?", startIndex + 1);
			charset = sb.substring(startIndex + 1, endIndex);
			return charset;
		}
		StringBuffer c = new StringBuffer(""); // 内容获取字符集
		getMailContent(message, c);
		if (c.toString().indexOf("charset") != -1) { // 从邮件正文获取字符集
			int index = c.toString().indexOf("charset");
			int startIndex = index + 8;
			int endIndex = 0;
			if (c.toString().indexOf("\"", startIndex + 1) > 0)
				endIndex = c.toString().indexOf("\"", startIndex + 1);
			else
				endIndex = c.toString().length();
			charset = c.toString().substring(startIndex, endIndex);
			if (charset.contains("\"")) { // 如果包含双引号
				StringBuffer sb2 = new StringBuffer(charset);
				sb2.deleteCharAt(0);
				charset = sb2.toString();
				if (charset.contains("/")) { // 如果包含"/"
					charset = "gb2312";
				}
			} else {
				if (charset.contains("/")) { // 如果包含"/"
					charset = "gb2312";
				}
			}
			charset = charset.trim();
			return charset;
		}
		charset = charset.trim();
		return charset;
	}

	/**
	 * 解析邮件正文内容
	 *
	 * @param part
	 * @param content
	 * @throws Exception
	 */
	public static void getMailContent(Part part, StringBuffer content) throws Exception {
		try {
			String contenttype = part.getContentType();
			int nameindex = contenttype.indexOf("name");
			boolean conname = false;
			if (nameindex != -1)
				conname = true;
			if (part.isMimeType("text/plain") && !conname) {
				content.append((String) part.getContent());
			} else if (part.isMimeType("text/html") && !conname) {
				content.append((String) part.getContent());
			} else if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();
				int counts = multipart.getCount();
				for (int i = 0; i < counts; i++) {
					getMailContent(multipart.getBodyPart(i), content);
				}
			} else if (part.isMimeType("message/rfc822")) {
				getMailContent((Part) part.getContent(), content);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 过滤字符串中的四字节码的表情，如新浪评论表情
	 *
	 * @param content
	 * @return
	 */
	public static String removeFourChar(String content) {
		byte[] conbyte = content.getBytes();
		for (int i = 0; i < conbyte.length; i++) {
			if ((conbyte[i] & 0xF8) == 0xF0) {
				for (int j = 0; j < 4; j++) {
					conbyte[i + j] = 0x30;
				}
				i += 3;
			}
		}
		content = new String(conbyte);
		return content.replaceAll("0000", "");
	}

	/**
	 * 处理发件人别名函数
	 *
	 * @param from
	 *            发件人
	 * @param message
	 *            外部邮件信息
	 * @return
	 * @throws Exception
	 */
	public static String handleFrom(String from, MimeMessage message) throws Exception {
		if (from.startsWith("=?") && from.endsWith("?=")) { // 如果以"=?"开始和"?="结束
			return handle(from);
		} else {
			String charset = getCharset(message); // 获取字符集
			if (from.equals(new String(from.getBytes(charset)))) {
				return from;
			} else {
				from = (new String(from.getBytes("iso-8859-1"), charset)); // 转成对应的字符集编码的字符
				return from;
			}
		}
	}

	/**
	 * 文件拷贝
	 *
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void copy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[1024];
		int len = 0;
		while ((len = is.read(bytes)) != -1) {
			os.write(bytes, 0, len);
		}
		if (os != null)
			os.close();
		if (is != null)
			is.close();
	}

	/**
	 * 处理主题乱码
	 * @param part
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	private String handlerSubject(Part part,String subject) throws Exception {
		if (subject != null) {
			if (subject.contains("=?") && subject.endsWith("?=")) { // 处理"=?...?="主题
				return MailTools.handle(subject);
			} else {
				String charset = MailTools.trans((MimeMessage) part); // 判断是否转码，需要转码则返回主题字符集

				if ("notrans".equals(charset)) { // 不需要转码
					return subject;
				} else {
					return new String(subject.getBytes("iso-8859-1"), charset); // 乱码主题转码
				}
			}
		} else {
			return "无主题";
		}
	}

	/**
	 * 处理邮件状态
	 * @param message
	 * @param mail
	 * @throws MessagingException
	 */
	private void handlerMailStatus(MimeMessage message,Mail mail) throws MessagingException {
		Flags flags = message.getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {//是否已阅读
            	mail.setReadFlag("1");
            }
            if(flag[i]==Flags.Flag.ANSWERED) {//是否已回复
            	mail.setReplyFlag("1");
            }
        }
	}

	/**
	 * 处理附件问题
	 * @param mail
	 * @param part
	 * @throws Exception
	 */
	private void handlerAttachMent(Mail mail,BodyPart part) throws Exception {
		String fileName = part.getFileName();
		if (fileName != null) {
			fileName = MailTools.handle(fileName.replaceAll("\r", "").replaceAll("\n", "")); // 处理附件名乱码问题
			InputStream is = part.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = is.read(buff, 0, 1024)) > 0) {
				out.write(buff, 0, rc);
			}
			mail.getMailAttachments().add(new MailAttachment(fileName, out.toByteArray()));
		}
	}


	public static String sendMail(String to, String subject, String content) throws Exception {
		if (to != null) {
			try {
				logger.error("111");
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
				final Properties p = System.getProperties();
				p.setProperty("mail.smtp.host","smtp.yeah.net");
				p.setProperty("mail.smtp.auth", "true");
				p.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
				p.setProperty("mail.smtp.socketFactory.fallback", "false");
				// 邮箱发送服务器端口,这里设置为465端口
				p.setProperty("mail.smtp.port", "465");
				p.setProperty("mail.smtp.socketFactory.port", "465");
				// 根据邮件会话属性和密码验证器构造一个发送邮件的session
				Session session = Session.getInstance(p, new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("liangchuanjiang@yeah.net",
								"lcj20190621");
					}
				});
				/*
				 * Properties props = System.getProperties();
				 * props.put("mail.smtp.host","smtp.126.com"); props.put("mail.smtp.auth",
				 * "true");
				 */
				session.setDebug(false);

				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress("liangchuanjiang@yeah.net"));
				// 邮件名
				message.setSubject(subject);
				// 发送到人
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				// 创建消息部分
				BodyPart messageBodyPart = new MimeBodyPart();
				// 消息
				messageBodyPart.setText(content);
				// 创建多重消息
				Multipart multipart = new MimeMultipart();
				// 设置文本消息部分
				multipart.addBodyPart(messageBodyPart);
				// 附件部分
				// 发送完整消息
				message.setContent(multipart);
				// 发送消息
				Transport.send(message);
				logger.error("333");
			} catch (Exception e) {
				logger.error("222",e);
				return "failure";
			}
			return "success";
		} else {
			return "failure";
		}
	}
}
