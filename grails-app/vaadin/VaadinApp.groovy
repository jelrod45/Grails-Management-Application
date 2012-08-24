package rapco

import rapco.*
import com.vaadin.*
import com.vaadin.ui.*
import com.vaadin.terminal.*
import com.vaadin.ui.Button.ClickEvent
import org.springframework.transaction.annotation.*

class VaadinApplication extends Application {

    transient springSecurityService
    def DBMService;
    private urole; def main, mainContent, ui;
    static transactional = true;

    void init(){
    	setTheme("custom");
        main = new Window("Rapid Committee Management System")
	main.setContent(buildLogin());
    }

    def buildLogin() {
	    setMainWindow(main)
	def layout = new VerticalLayout();
	    def image = new Embedded("", new ExternalResource(new URL("http://rapidcommittee.com/static/kJC8yvlXeMw0SeONV3a4WfTNBuXAGvqDmXfwuHYZAJ.png")));
	    	image.setStyleName("mainLogo");
	    layout.addComponent(image);
	    layout.setComponentAlignment(image, Alignment.BOTTOM_CENTER);

        def loginPane = new Panel()
	    loginPane.setWidth("300px");
	    
	def loginForm = new FormLayout();
	    def usernameField = new TextField("Username:");
	    	loginForm.addComponent(usernameField);
	    def passwordField = new PasswordField("Password:");
	        loginForm.addComponent(passwordField);
	    loginForm.addComponent(new Button("Login",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
		    def errno = authenticateUser(usernameField.getValue(), passwordField.getValue());
		    def formComps = getAllComponents(loginForm);
		    if (formComps[formComps.size-1].getClass() == Label)
		        loginForm.removeComponent(formComps[formComps.size-1]);
		    if (errno == -1) {
		        def err = new Label("* Invalid username/password.");
			    err.setStyleName("error");
			loginForm.addComponent(err);
			return;
		    }
	        }
	    }));
	
	loginPane.setContent(loginForm);
	layout.addComponent(loginPane);
	layout.setComponentAlignment(loginPane, Alignment.TOP_CENTER);
	usernameField.focus();
	return layout;
    }

    
    void startApplication() {
    	def mainLayout = new VerticalLayout();
	def headerBar = new HorizontalLayout();
	    headerBar.setSizeFull();
	    headerBar.setHeight("35px");
	    headerBar.setStyleName("mainHeader");
	    def header = new Label("Rapid Committee Management System : Logged in as " + urole.user.username);
	    headerBar.addComponent(header);
	    def but = new Button("Logout",
	        new Button.ClickListener() {
		public void buttonClick(ClickEvent e) {
		    urole = null;
		    getMainWindow().setContent(buildLogin());
		}
	    });
		but.setStyleName("logout");
	    headerBar.addComponent(but);
	    mainLayout.addComponent(headerBar);
    	    mainContent = new HorizontalLayout();
	        def menu = buildMenu();
	        mainContent.addComponent(menu);
		def vr = new Panel("")
		    vr.setWidth("2px");
		    vr.setStyleName("verticalRule");
		mainContent.addComponent(vr);
		def l = new Label("");
		    l.setSizeFull();
		mainContent.addComponent(l);
	    mainLayout.addComponent(mainContent);
        getMainWindow().setContent(mainLayout);
    }

    def buildMenu() {
    	VerticalLayout menu = new VerticalLayout();
	    def image = new Embedded("", new ExternalResource(new URL("http://rapidcommittee.com/static/kJC8yvlXeMw0SeONV3a4WfTNBuXAGvqDmXfwuHYZAJ.png")));
	    	image.setWidth("135px");
	        image.setStyleName("littleLogo");
	    menu.addComponent(image);
	    def button = new Button("Committees",
                new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	 	    showCommitteesUI();
	        }
	    });
		button.setStyleName("menuButton");
	    menu.addComponent(button);
		button = new Button("Members",
                new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	 	    showMembersUI();
	        }
	    });
		button.setStyleName("menuButton");
	    menu.addComponent(button);
            
	    if (urole.role.authority == "ROLE_ADMIN") {
	        button = new Button("Users",
                new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	 	    showUsersUI();
	        }
	    });
		button.setStyleName("menuButton");
	    menu.addComponent(button);
	    }
	    def space = new Label("");
	    	space.setHeight("40px");
	    menu.addComponent(space);
	    for (component in getAllComponents(menu)) {
                menu.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
	    }
	    menu.setStyleName("mainMenu");
	    return menu;
    }

    //COMMITTEES FUNCTIONS  //COMMITTEES FUNCTIONS  //COMMITTEES FUNCTIONS

    def showCommitteesUI() {
    	def comps = getAllComponents(mainContent);
	mainContent.removeComponent(comps[comps.size-1]);
	ui = new VerticalLayout();
	    def uiLinks = new HorizontalLayout();
		def button = new Button("View Committees",
		    new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	 	        committeesShowAll();
	            }
	        });
		button.setStyleName("navButton");
		uiLinks.addComponent(button);
		    button = new Button("Add New Committee",
		    new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	 	        committeesShowAdd();
	            }
	        });
		button.setStyleName("navButton");
		uiLinks.addComponent(button);
	    ui.addComponent(uiLinks);
	    Label l = new Label("");
	    	  l.setSizeFull();
	    for (component in getAllComponents(uiLinks))
	        uiLinks.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
	    ui.addComponent(l);
	mainContent.addComponent(ui);
    }

    def committeesShowAll() {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
        def committees = rapco.Committee.getAll();
	    committees.sort { a,b -> a.name <=> b.name }
    	def list = new VerticalLayout();
	    def header = new HorizontalLayout();
	    def data = new Label("Committee Name");
	        data.setStyleName("listHeader");
		data.setWidth("120px");
	    header.addComponent(data);
	        data = new Label("Committee Description");
	        data.setStyleName("listHeader");
		data.setWidth("160px");
	    header.addComponent(data);
	        data = new Label("Committee Bylaws");
	        data.setStyleName("listHeader");
		data.setWidth("200px");
	    header.addComponent(data);
	    list.addComponent(header);
            for (committee in committees) {
	    	def cname = committee.name;
		def cdesc = committee.description;
		def cbylaws = committee.bylaws;
	    	def item = new HorizontalLayout();
		    data = new Label(committee.name);
		    data.setStyleName("listItem");
		    data.setWidth("120px");
		    item.addComponent(data);
		    data = new Label(committee.description);
		    data.setStyleName("listItem");
		    data.setWidth("160px");
		    item.addComponent(data);
		    data = new Label(committee.bylaws);
		    data.setStyleName("listItem");
		    data.setWidth("200px");
		    item.addComponent(data);
		    def button = new Button("Edit",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    committeesShowEdit(cname, cdesc, cbylaws);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		        button = new Button("Manage Members",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    committeesShowManage(cname);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
			button = new Button("Delete",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    comps = getAllComponents(ui);
			    ui.removeComponent(comps[comps.size-1]);
			    def confirm = new VerticalLayout();
		    	    def msg = new Label("Are you sure you want to delete " + cname + "?");
			    msg.setStyleName("onSuccess");
			    confirm.addComponent(msg);
			    button = new Button("Yes, permanently delete this committee",
				new Button.ClickListener() {
			    	public void buttonClick(ClickEvent event2) {
				    deleteCommittee(cname);
		    		    showMessage("Committee successfully deleted.");
			        }
			    });
			    button.setStyleName("navButton");
			    confirm.addComponent(button);
			    ui.addComponent(confirm);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		list.addComponent(item);
	    }
	ui.addComponent(list);
    }

    def committeesShowAdd() {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def form = new FormLayout();
	    def name = new TextField("  Committee Name: ");
	    	name.setWidth("250px");
	    form.addComponent(name);
	    def desc = new TextArea("  Committee Description: ");
	    	desc.setWidth("400px");
		desc.setRows(3);
	    form.addComponent(desc);
	    def bylaws = new TextArea("  Committee Bylaws: ");
	    	bylaws.setWidth("400px");
		bylaws.setRows(10);
	    form.addComponent(bylaws);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button(  "Add Committee",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            def errno = DBMService.saveCommittee(name.getValue(), desc.getValue(), bylaws.getValue());
		    def formComps = getAllComponents(form);
		    if (formComps[formComps.size-1].getClass() == Label)
		        form.removeComponent(formComps[formComps.size-1]);
		    if (errno == -2) {
		        def err = new Label("* Committee already exists. Check name entry.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -1) {
		        def err = new Label("* Save failed, possibly because of illegal symbols in fields.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    showMessage("New committee saved successfully.");
	        }
	    }));
	ui.addComponent(form);
    }

    def committeesShowAddMember(cname) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def form = new FormLayout();
	    def lname = new TextField("Last Name: ");
	    	lname.setWidth("90px");
	    form.addComponent(lname);
	    def fname = new TextField("First Name: ");
	    	fname.setWidth("90px");
	    form.addComponent(fname);
	    def title = new TextField("Title: ");
	    	title.setWidth("160px");
	    form.addComponent(title);
	    def startDate = new PopupDateField("Start Date: ");
	    form.addComponent(startDate);
	    def endDate = new PopupDateField("End Date: ");
	    form.addComponent(endDate);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Add Member",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            def errno = DBMService.saveCommitteeMember(cname, lname.getValue(), fname.getValue(), title.getValue(), startDate.getValue(), endDate.getValue());
		    def formComps = getAllComponents(form);
		    if (formComps[formComps.size-1].getClass() == Label)
		        form.removeComponent(formComps[formComps.size-1]);
		    if (errno == -3) {
		        def err = new Label("* Member is already enrolled in this committee.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -2) {
		        def err = new Label("* Member does not exist. Check name spelling.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -1) {
		        def err = new Label("* Save failed, possibly because of illegal symbols in fields.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    def vert = showMessage(lname.getValue() + ", " + fname.getValue() + " successfully added to " + cname + ".");
		    def back = new Button("Back to " + cname,						new Button.ClickListener() {
		                   public void buttonClick(ClickEvent event3) {
			               committeesShowManage(cname);
			           }
			       });
			back.setStyleName("navButton");
			vert.addComponent(back);
	        }
	    }));
	ui.addComponent(form);
    }
    	
    def committeesShowEdit(oldName, oldDesc, oldBylaws) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def formContainer = new VerticalLayout();
	    def commName = new Label(oldName);
	    	commName.setStyleName("onSuccess");
	formContainer.addComponent(commName);
	def form = new FormLayout();
	    def desc = new TextArea("  Committee Description: ");
	    	desc.setWidth("400px");
		desc.setRows(3);
		desc.setValue(oldDesc);
	    form.addComponent(desc);
	    def bylaws = new TextArea("  Committee Bylaws: ");
	    	bylaws.setWidth("400px");
		bylaws.setRows(10);
		bylaws.setValue(oldBylaws);
	    form.addComponent(bylaws);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Save Changes",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            DBMService.updateCommittee(oldName, desc.getValue(), bylaws.getValue());
		    showMessage("Changes saved successfully.");
		    
	        }
	    }));
	formContainer.addComponent(form);
	ui.addComponent(formContainer);
    }

    def committeesShowManage(cname) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
        def cmembers = CommitteeMember.findAllByCommittee(Committee.findByName(cname));
	    cmembers.sort { a,b -> 
	        if ( a.member.lastName == b.member.lastName )
		    a.member.firstName <=> b.member.firstName
		else
		    a.member.lastName <=> b.member.lastName
            }
    	def list = new VerticalLayout();
	    def header = new HorizontalLayout();
	    	def lab = new Label(cname);
	    	    lab.setStyleName("onSuccess");
	    	header.addComponent(lab);
	    def button = new Button("Add new member",
	            new Button.ClickListener() {
		    public void buttonClick(ClickEvent event) {
		        committeesShowAddMember(cname);
	            }
	   	});
		button.setStyleName("navButton");
		header.addComponent(button);
	    list.addComponent(header);
	        header = new HorizontalLayout();
	    def data = new Label("Name");
	        data.setStyleName("listHeader");
		data.setWidth("100px");
	    header.addComponent(data);
	        data = new Label("Title");
	        data.setStyleName("listHeader");
		data.setWidth("100px");
	    header.addComponent(data);
		data = new Label("Start Date");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
		data = new Label("End Date");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
	        data = new Label("Created");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
	        data = new Label("Updated");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
	    list.addComponent(header);
	    for (cmember in cmembers) {
	    	def mem = cmember.member;
	       	def fname = cmember.member.firstName;
	    	def lname = cmember.member.lastName;
		def address = cmember.member.address;
		def phone = cmember.member.phone;
		def title = cmember.title;
		def sdate = cmember.startDate;
		def edate = cmember.endDate;
		def created = cmember.created.getDateString();
		def updated = cmember.updated.getDateString();
	    	def item = new HorizontalLayout();
		    data = new Label(lname + ", " + fname);
		    data.setStyleName("listItem");
		    data.setWidth("100px");
		    item.addComponent(data);
		    data = new Label(title);
		    data.setStyleName("listItem");
		    data.setWidth("100px");
		    item.addComponent(data);
		    data = new Label(sdate.getDateString());
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		    data = new Label(edate.getDateString());
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		    data = new Label(created);
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		    data = new Label(updated);
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		        button = new Button("Edit",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    committeesManageMembersShowEdit(mem, cname, title, sdate, edate);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
			button = new Button("Delete",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    comps = getAllComponents(ui);
			    ui.removeComponent(comps[comps.size-1]);
			    def confirm = new VerticalLayout();
		    	    def msg = new Label("Are you sure you want to remove " + lname + ", " + fname + " from " + cname + "?");
			    msg.setStyleName("onSuccess");
			    confirm.addComponent(msg);
			    button = new Button("Yes, remove this member",
				new Button.ClickListener() {
			    	public void buttonClick(ClickEvent event2) {
				    deleteCommitteeMember(mem, cname);
				    def vert = showMessage("Member successfully removed.");
				    def back = new Button("Back to " + cname,						new Button.ClickListener() {
				        public void buttonClick(ClickEvent event3) {
				            committeesShowManage(cname);
				        }
				    });
				    back.setStyleName("navButton");
				    vert.addComponent(back);
			        }
			    });
			    button.setStyleName("navButton");
			    confirm.addComponent(button);
			    ui.addComponent(confirm);
			    }
			});
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		list.addComponent(item);
	    }
	ui.addComponent(list);
    }


    //END COMMITTEES

    //START MEMBERS

    def showMembersUI() {
    	def comps = getAllComponents(mainContent);
	mainContent.removeComponent(comps[comps.size-1]);
	ui = new VerticalLayout();
	    def uiLinks = new HorizontalLayout();
		def button = new Button("View Members",
		    new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	 	        membersShowAll();
	            }
	        });
		button.setStyleName("navButton");
		uiLinks.addComponent(button);
		    button = new Button("Add New Member",
		    new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	 	        membersShowAdd();
	            }
	        });
		button.setStyleName("navButton");
		uiLinks.addComponent(button);
	    ui.addComponent(uiLinks);
	    Label l = new Label("");
	    	  l.setSizeFull();
	    for (component in getAllComponents(uiLinks))
	        uiLinks.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
	    ui.addComponent(l);
	mainContent.addComponent(ui);
    }

    def membersShowAll() {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
        def members = rapco.Member.getAll();
	    members.sort { a,b -> 
	        if (a.lastName == b.lastName)
		    a.firstName <=> b.firstName
		else
		    a.lastName <=> b.lastName
	    }
    	def list = new VerticalLayout();
	    def header = new HorizontalLayout();
	    def data = new Label("Last Name");
	        data.setStyleName("listHeader");
		data.setWidth("90px");
	    header.addComponent(data);
		data = new Label("First Name");
	        data.setStyleName("listHeader");
		data.setWidth("90px");
	    header.addComponent(data);
	        data = new Label("Middle Name");
	        data.setStyleName("listHeader");
		data.setWidth("90px");
	    header.addComponent(data);
	        data = new Label("Address");
	        data.setStyleName("listHeader");
		data.setWidth("200px");
	    header.addComponent(data);
	        data = new Label("Phone");
	        data.setStyleName("listHeader");
		data.setWidth("90px");
	    header.addComponent(data);
	    list.addComponent(header);
            for (member in members) {
	    	def mem = member;
	    	def fname = member.firstName;
	    	def mname = member.middleName;
	    	def lname = member.lastName;
		def address = member.address;
		def phone = member.phone;
	    	def item = new HorizontalLayout();
		    data = new Label(member.lastName);
		    data.setStyleName("listItem");
		    data.setWidth("90px");
		    item.addComponent(data);
		    data = new Label(member.firstName);
		    data.setStyleName("listItem");
		    data.setWidth("90px");
		    item.addComponent(data);
		    data = new Label(member.middleName);
		    data.setStyleName("listItem");
		    data.setWidth("90px");
		    item.addComponent(data);
		    data = new Label(address);
		    data.setStyleName("listItem");
		    data.setWidth("200px");
		    item.addComponent(data);
		    data = new Label(phone);
		    data.setStyleName("listItem");
		    data.setWidth("90px");
		    item.addComponent(data);
		    def button = new Button("Edit",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    membersShowEdit(fname, mname, lname, address, phone);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		        button = new Button("Manage Committees",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    membersShowManage(mem);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
			button = new Button("Delete",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    comps = getAllComponents(ui);
			    ui.removeComponent(comps[comps.size-1]);
			    def confirm = new VerticalLayout();
		    	    def msg = new Label("Are you sure you want to delete " + lname + ", " + fname + " ?");
			    msg.setStyleName("onSuccess");
			    confirm.addComponent(msg);
			    button = new Button("Yes, permanently delete this committee",
				new Button.ClickListener() {
			    	public void buttonClick(ClickEvent event2) {
				    deleteMember(fname, lname);
		    		    showMessage("Member successfully deleted.");
			        }
			    });
			    button.setStyleName("navButton");
			    confirm.addComponent(button);
			    ui.addComponent(confirm);
			    }
			});
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		list.addComponent(item);
	    }
	ui.addComponent(list);
    }

    def membersShowAdd() {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def form = new FormLayout();
	    def	lname = new TextField("Last Name: ");
	    	lname.setWidth("100px");
	    form.addComponent(lname);
	    def fname = new TextField("First Name: ");
	    	fname.setWidth("100px");
	    form.addComponent(fname);
	    def mname = new TextField("Middle Name: ");
	    	mname.setWidth("100px");
	    form.addComponent(mname);
	    def address = new TextArea("Address: ");
	    	address.setWidth("200px");
		address.setRows(3);
	    form.addComponent(address);
	    def	phone = new TextField("Phone: ");
	    	phone.setWidth("100px");
	    form.addComponent(phone);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Add Member",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
		    def errno = DBMService.saveMember(fname.getValue(), mname.getValue(), lname.getValue(), address.getValue(), phone.getValue());
		    def formComps = getAllComponents(form);
		    if (formComps[formComps.size-1].getClass() == Label)
		        form.removeComponent(formComps[formComps.size-1]);
		    if (errno == -2) {
		        def err = new Label("* Member already exists. Check name entry.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -1) {
		        def err = new Label("* Save failed, possibly because of illegal symbols in fields.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    showMessage("New member saved successfully.");
	        }
	    }));
	ui.addComponent(form);
    }

    def membersShowAddCommittee(member) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def form = new FormLayout();
	    def cname = new TextField("Committee Name: ");
	    	cname.setWidth("90px");
	    form.addComponent(cname);
	    def title = new TextField("Title: ");
	    	title.setWidth("160px");
	    form.addComponent(title);
	    def startDate = new PopupDateField("Start Date: ");
	    form.addComponent(startDate);
	    def endDate = new PopupDateField("End Date: ");
	    form.addComponent(endDate);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Enroll in Committee",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            def errno = DBMService.saveCommitteeMember(cname.getValue(), member.lastName, member.firstName, title.getValue(), startDate.getValue(), endDate.getValue());
		    def formComps = getAllComponents(form);
		    if (formComps[formComps.size-1].getClass() == Label)
		        form.removeComponent(formComps[formComps.size-1]);
		    if (errno == -3) {
		        def err = new Label("* Member is already enrolled in this committee.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -2) {
		        def err = new Label("* Committee does not exist. Check name spelling.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -1) {
		        def err = new Label("* Save failed, possibly because of illegal symbols in fields.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    def vert = showMessage(member.lastName + ", " + member.firstName + " successfully added to " + cname + ".");
		    def back = new Button("Back to " + member.lastName + ", " + member.firstName,
		    	       new Button.ClickListener() {
		                   public void buttonClick(ClickEvent event3) {
			               membersShowManage(member);
			           }
			       });
			back.setStyleName("navButton");
			vert.addComponent(back);
	        }
	    }));
	ui.addComponent(form);    	
    }

    def membersShowEdit(oldFname, oldMname, oldLname, oldAddress, oldPhone) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def formContainer = new VerticalLayout();
	    def memName = new Label(oldLname + ", " + oldFname);
	    	memName.setStyleName("onSuccess");
	formContainer.addComponent(memName);
	def form = new FormLayout();
	    def address = new TextArea("Address: ");
	    	address.setValue(oldAddress);
	    	address.setWidth("200px");
		address.setRows(3);
	    form.addComponent(address);
	    def	phone = new TextField("Phone: ");
	    	phone.setValue(oldPhone);
	    	phone.setWidth("100px");
	    form.addComponent(phone);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Save Changes",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            DBMService.updateMember(oldLname, oldFname, oldFname, oldMname, oldLname, address.getValue(), phone.getValue());
		    showMessage("Changes saved successfully.");
	        }
	    }));
	formContainer.addComponent(form);
	ui.addComponent(formContainer);
    }

    def membersShowManage(member) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def memberships = CommitteeMember.findAllByMember(member);
	    memberships.sort { a,b -> a.committee.name <=> b.committee.name }
    	def list = new VerticalLayout();
	    def header = new HorizontalLayout();
	    	def lab = new Label(member.lastName + ", " + member.firstName);
	    	    lab.setStyleName("onSuccess");
	    	header.addComponent(lab);
	    def button = new Button("Enroll in New Committee",
	            new Button.ClickListener() {
		    public void buttonClick(ClickEvent event) {
		        membersShowAddCommittee(member);
	            }
	   	});
		button.setStyleName("navButton");
		header.addComponent(button);
	    list.addComponent(header);
	        header = new HorizontalLayout();
	    def data = new Label("Committee");
	        data.setStyleName("listHeader");
		data.setWidth("100px");
	    header.addComponent(data);
	        data = new Label("Title");
	        data.setStyleName("listHeader");
		data.setWidth("100px");
	    header.addComponent(data);
		data = new Label("Start Date");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
		data = new Label("End Date");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
	        data = new Label("Created");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
	        data = new Label("Updated");
	        data.setStyleName("listHeader");
		data.setWidth("70px");
	    header.addComponent(data);
	    list.addComponent(header);
	    for (membership in memberships) {
	    	def mem = membership.member;
		def cname = membership.committee.name
		def title = membership.title;
		def sdate = membership.startDate;
		def edate = membership.endDate;
		def created = membership.created.getDateString();
		def updated = membership.updated.getDateString();
	    	def item = new HorizontalLayout();
		    data = new Label(cname);
		    data.setStyleName("listItem");
		    data.setWidth("100px");
		    item.addComponent(data);
		    data = new Label(title);
		    data.setStyleName("listItem");
		    data.setWidth("100px");
		    item.addComponent(data);
		    data = new Label(sdate.getDateString());
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		    data = new Label(edate.getDateString());
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		    data = new Label(created);
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		    data = new Label(updated);
		    data.setStyleName("listItem");
		    data.setWidth("70px");
		    item.addComponent(data);
		        button = new Button("Edit",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    membersManageCommitteesShowEdit(mem, cname, title, sdate, edate);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
			button = new Button("Delete",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    comps = getAllComponents(ui);
			    ui.removeComponent(comps[comps.size-1]);
			    def confirm = new VerticalLayout();
		    	    def msg = new Label("Are you sure you want to remove " + member.lastName + ", " + member.firstName + " from " + cname + "?");
			    msg.setStyleName("onSuccess");
			    confirm.addComponent(msg);
			    button = new Button("Yes, remove this member",
				new Button.ClickListener() {
			    	public void buttonClick(ClickEvent event2) {
				    deleteCommitteeMember(mem, cname);
				    def vert = showMessage("Successfully removed from " + cname + ".");
				    def back = new Button("Back to " + member.lastName + ", " + member.firstName,						new Button.ClickListener() {
				        public void buttonClick(ClickEvent event3) {
				            membersShowManage(member);
				        }
				    });
				    back.setStyleName("navButton");
				    vert.addComponent(back);
			        }
			    });
			    button.setStyleName("navButton");
			    confirm.addComponent(button);
			    ui.addComponent(confirm);
			    }
			});
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		list.addComponent(item);
	    }
	ui.addComponent(list);
    }

    //END MEMBERS

    //START COMMITTEEMEMBERS

    def committeesManageMembersShowEdit(member, cname, oldTitle,  oldStart, oldEnd) {        
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def formContainer = new VerticalLayout();
	    def commName = new Label(cname + "; " + member.lastName + ", " + member.firstName);
	    	commName.setStyleName("onSuccess");
	formContainer.addComponent(commName);
	def form = new FormLayout();
	    def titleField = new TextField("Title: ");
	    	titleField.setValue(oldTitle);
	    form.addComponent(titleField);
	    def startDate = new PopupDateField("Start Date: ");
	    	startDate.setValue(oldStart);
	    form.addComponent(startDate);
	    def endDate = new PopupDateField("End Date: ");
	    	endDate.setValue(oldEnd);
	    form.addComponent(endDate);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Save Changes",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            DBMService.updateCommitteeMember(member, cname, titleField.getValue(), startDate.getValue(), endDate.getValue());
		    def vert = showMessage("Changes saved successfully.");
		    def back = new Button("Back to " + cname,
         		    new Button.ClickListener() {
		            public void buttonClick(ClickEvent event3) {
			            committeesShowManage(cname);
			    }
	                });
		    back.setStyleName("navButton");
		    vert.addComponent(back);
	        }
	    }));
	formContainer.addComponent(form);
	ui.addComponent(formContainer);
    }

    def membersManageCommitteesShowEdit(member, cname, oldTitle,  oldStart, oldEnd) {        
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def formContainer = new VerticalLayout();
	    def commName = new Label(cname + "; " + member.lastName + ", " + member.firstName);
	    	commName.setStyleName("onSuccess");
	formContainer.addComponent(commName);
	def form = new FormLayout();
	    def titleField = new TextField("Title: ");
	    	titleField.setValue(oldTitle);
	    form.addComponent(titleField);
	    def startDate = new PopupDateField("Start Date: ");
	    	startDate.setValue(oldStart);
	    form.addComponent(startDate);
	    def endDate = new PopupDateField("End Date: ");
	    	endDate.setValue(oldEnd);
	    form.addComponent(endDate);
	    def space = new Label("");
	    	space.setHeight("20px");
	    form.addComponent(new Button("Save Changes",
	        new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            DBMService.updateCommitteeMember(member, cname, titleField.getValue(), startDate.getValue(), endDate.getValue());
		    def vert = showMessage("Changes saved successfully.");
		    def back = new Button("Back to " + member.lastName + ", " + member.firstName,
         		    new Button.ClickListener() {
		            public void buttonClick(ClickEvent event3) {
			            membersShowManage(member);
			    }
	                });
		    back.setStyleName("navButton");
		    vert.addComponent(back);
	        }
	    }));
	formContainer.addComponent(form);
	ui.addComponent(formContainer);
    }

//END COMMITTEEMEMBER

//START USERS

    def showUsersUI() {
    	def comps = getAllComponents(mainContent);
	mainContent.removeComponent(comps[comps.size-1]);
	ui = new VerticalLayout();
	    def uiLinks = new HorizontalLayout();
		def button = new Button("View Users",
		    new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	 	        usersShowAll();
	            }
	        });
		button.setStyleName("navButton");
		uiLinks.addComponent(button);
		    button = new Button("Add New User",
		    new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	 	        usersShowAdd();
	            }
	        });
		button.setStyleName("navButton");
		uiLinks.addComponent(button);
	    ui.addComponent(uiLinks);
	    Label l = new Label("");
	    	  l.setSizeFull();
	    for (component in getAllComponents(uiLinks))
	        uiLinks.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
	    ui.addComponent(l);
	mainContent.addComponent(ui);
    }

    def usersShowAll() {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
        def users = rapco.User.getAll();
	    users.sort { a,b -> a.username <=> b.username }
    	def list = new VerticalLayout();
	    def header = new HorizontalLayout();
	    def data = new Label("Username");
	        data.setStyleName("listHeader");
		data.setWidth("90px");
	    header.addComponent(data);
	    list.addComponent(header);
            for (user in users) {
	    	def u = user
	    	def item = new HorizontalLayout();
		    data = new Label(user.username);
		    data.setStyleName("listItem");
		    data.setWidth("90px");
		    item.addComponent(data);
		    def button = new Button("Edit",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    usersShowEdit(u);
	        	}
	   	    });
		    	button.setStyleName("listItem");
		    item.addComponent(button);
			button = new Button("Delete",
	            	new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			    comps = getAllComponents(ui);
			    ui.removeComponent(comps[comps.size-1]);
			    def confirm = new VerticalLayout();
		    	    def msg = new Label("Are you sure you want to delete" + user.username + "?");
			    msg.setStyleName("onSuccess");
			    confirm.addComponent(msg);
			    button = new Button("Yes, permanently delete this user",
				new Button.ClickListener() {
			    	public void buttonClick(ClickEvent event2) {
				    deleteUser(u);
		    		    showMessage("User account successfully deleted.");
			        }
			    });
			    button.setStyleName("navButton");
			    confirm.addComponent(button);
			    ui.addComponent(confirm);
			    }
			});
		    	button.setStyleName("listItem");
		    item.addComponent(button);
		list.addComponent(item);
	    }
	ui.addComponent(list);
    }    

    def usersShowAdd() {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def form = new FormLayout();
	def formContainer = new VerticalLayout();
	    def userName = new TextField("Username: ");
	    form.addComponent(userName);
	    def newPass = new PasswordField("Password: ");
	    form.addComponent(newPass);
	    def role = new Select("User Permissions: ");
	    	role.addItem("User");
		role.addItem("Admin");
            form.addComponent(role);
	    form.addComponent(new Button("Save User",
	        new Button.ClickListener() {
		public void buttonClick(ClickEvent e) {
		    def errno = DBMService.saveUser(userName.getValue(), newPass.getValue(), role.getValue());
		    def formComps = getAllComponents(form);
		    if (formComps[formComps.size-1].getClass() == Label)
		        form.removeComponent(formComps[formComps.size-1]);
		    if (errno == -2) {
		        def err = new Label("* A user by that username already exists.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
		    if (errno == -1) {
		        def err = new Label("* There was a problem updating this user. Please reload and try again.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
 	            showMessage("New user added successfully.");
		}
	    }));
	formContainer.addComponent(form);
	ui.addComponent(formContainer);	
    }

    def usersShowEdit(user) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def userRole = UserRole.findByUser(user);
	def formContainer = new VerticalLayout();
	    def userName = new Label(user.username);
	    	userName.setStyleName("onSuccess");
	formContainer.addComponent(userName);
	def form = new FormLayout();
	    def newPass = new PasswordField("New Password: (leave blank to keep current)");
	    form.addComponent(newPass);
	    def role = new Select("User Permissions: ");
	    	role.addItem("User");
		role.addItem("Admin");
	    	if (userRole.role.authority == "ROLE_ADMIN") {
		    role.setValue("Admin");
		}
		else {
		    role.setValue("User");
                }
            form.addComponent(role);
	    form.addComponent(new Button("Update",
	        new Button.ClickListener() {
		public void buttonClick(ClickEvent e) {
		    def errno = DBMService.updateUser(user, newPass.getValue(), role.getValue());
		    def formComps = getAllComponents(form);
		    if (formComps[formComps.size-1].getClass() == Label)
		        form.removeComponent(formComps[formComps.size-1]);
		    if (errno == -1) {
		        def err = new Label("* There was a problem updating this user. Please reload and try again.");
			    err.setStyleName("error");
			form.addComponent(err);
			return;
		    }
 	            showMessage("User updated successfully.");
		}
	    }));
	formContainer.addComponent(form);
	ui.addComponent(formContainer);	
    }

//END USERS

    def authenticateUser(username, password) {
    	def currentUser = rapco.User.findByUsername(username);
	if ((currentUser.getClass() != User) || (currentUser.password != springSecurityService.encodePassword(password)))
	   return -1;
	else {
	    urole = rapco.UserRole.findByUser(currentUser);
	    startApplication();
	}
    }

    def deleteCommittee(cname) {
    	DBMService.deleteCommittee(cname);
    }

    def deleteMember(fname, lname) {
    	DBMService.deleteMember(fname, lname);
    }

    def deleteCommitteeMember(member, cname) {
    	DBMService.deleteCommitteeMember(member, cname);
    }

    def deleteUser(user) {
    	DBMService.deleteUser(user);
    }

    def showMessage(message) {
    	def comps = getAllComponents(ui);
	ui.removeComponent(comps[comps.size-1]);
	def vert = new VerticalLayout()
	    def l = new Label(message);
	    	l.setStyleName("onSuccess");
	    vert.addComponent(l);
	ui.addComponent(vert);
	return vert;
    }

    def getAllComponents(parent) {
    	def results = [];
        def iter = parent.getComponentIterator();
	while (iter.hasNext()) {
	    def elem = iter.next();
	    results.add(elem);
	}
	return results;
    }

}