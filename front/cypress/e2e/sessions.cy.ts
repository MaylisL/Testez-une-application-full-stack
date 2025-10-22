describe('Sessions', () => {
  const user = {
    id: 1,
    username: 'user1',
    firstName: 'firstname1',
    lastName: 'lastname1',
    email: 'user@demo.com',
    admin: false,
  };

  const admin = {
    id: 2,
    username: 'admin1',
    firstName: 'adminFirst',
    lastName: 'adminLast',
    email: 'admin@demo.com',
    admin: true,
  };

  const mockSessions = [
    {
      id: 101,
      name: 'Morning Yoga',
      date: '2025-05-01T00:00:00Z',
      description: 'Sunrise yoga for energy.',
      teacher_id: 10,
      users: [],
    },
    {
      id: 102,
      name: 'Evening Meditation',
      date: '2025-05-02T00:00:00Z',
      description: 'Relax before sleep.',
      teacher_id: 11,
      users: [1],
    },
  ];

  const mockTeacher = {
    id: 10,
    firstName: 'TeacherName',
    lastName: 'TeacherLastName',
  };

  it('shows session list for a normal user (no Create or Edit buttons)', () => {
    cy.login(user, mockSessions);

    cy.contains('Rentals available').should('be.visible');
    cy.contains('Morning Yoga').should('be.visible');
    cy.contains('Evening Meditation').should('be.visible');

    cy.contains('Create').should('not.exist');

    cy.get('mat-card-actions button').contains('Edit').should('not.exist');
  });

  it('shows session list for an admin (Create + Edit buttons visible)', () => {
    cy.login(admin, mockSessions);

    cy.contains('Rentals available').should('be.visible');
    cy.contains('Create').should('be.visible');

    cy.get('mat-card-actions button').contains('Edit').should('exist');
  });

  it('shows detail page for a normal user', () => {
    cy.login(user, mockSessions);

    cy.intercept('GET', '/api/session/101', {
      statusCode: 200,
      body: mockSessions[0],
    }).as('getSessionDetail');

    cy.intercept('GET', '/api/teacher/10', {
      statusCode: 200,
      body: mockTeacher,
    }).as('getTeacher');

    cy.contains('button', 'Detail').first().click();
    cy.wait('@getSessionDetail');
    cy.wait('@getTeacher');
    cy.url().should('include', '/sessions/detail/101');

    cy.contains('Morning Yoga').should('be.visible');
    cy.contains('TeacherName TEACHERLASTNAME').should('be.visible');
    cy.contains('Description:').should('be.visible');

    cy.contains('Participate').should('be.visible');
    cy.contains('Delete').should('not.exist');
  });

  it('shows detail page for an admin', () => {
    cy.login(admin, mockSessions);

    cy.intercept('GET', '/api/session/101', {
      statusCode: 200,
      body: mockSessions[0],
    }).as('getSessionDetail');

    cy.intercept('GET', '/api/teacher/10', {
      statusCode: 200,
      body: mockTeacher,
    }).as('getTeacher');

    cy.contains('button', 'Detail').first().click();
    cy.wait('@getSessionDetail');
    cy.wait('@getTeacher');

    cy.contains('Delete').should('be.visible');
    cy.contains('Participate').should('not.exist');
  });


  it('shows create form for admin', () => {
    cy.login(admin);

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [mockTeacher],
    }).as('getTeachers');

    cy.contains('button', 'Create').first().click();

    cy.wait('@getTeachers');

    cy.contains('Create session').should('be.visible');


    cy.get('input[formControlName=name]').should('exist');
    cy.get('input[formControlName=date]').should('exist');
    cy.get('mat-select[formControlName=teacher_id]').should('exist');
    cy.get('textarea[formControlName=description]').should('exist');


    cy.get('input[formControlName=name]').type('New Yoga');
    cy.get('input[formControlName=date]').type('2025-06-10');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.contains('TeacherName TeacherLastName').click();
    cy.get('textarea[formControlName=description]').type('Morning class');

    cy.intercept('POST', '/api/session', {
      statusCode: 201,
      body: {},
    }).as('createSession');

    cy.get('button[type="submit"]').click();
    cy.wait('@createSession');
  });

  it('allows admin to edit a session', () => {
    cy.login(admin, mockSessions);
    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [mockTeacher],
    }).as('getTeachers');

    cy.intercept('GET', '/api/session/101', {
      statusCode: 200,
      body: mockSessions[0],
    }).as('getSessionDetail');

    cy.contains('mat-card', 'Morning Yoga')
      .find('button')
      .contains('Edit')
      .click();

    cy.wait('@getSessionDetail');
    cy.wait('@getTeachers');

    cy.get('input[formControlName=name]').should('have.value', 'Morning Yoga');
    cy.get('input[formControlName=date]').should('have.value', '2025-05-01');
    cy.get('mat-select[formControlName=teacher_id]').contains('TeacherName TeacherLastName');

    cy.get('input[formControlName=name]').clear().type('Updated Yoga');
    cy.get('textarea[formControlName=description]').clear().type('Updated description');

    cy.intercept('PUT', '/api/session/101', (req) => {
      expect(req.body.name).to.equal('Updated Yoga');
      expect(req.body.description).to.equal('Updated description');
      req.reply({ statusCode: 200, body: {} });
    }).as('updateSession');

    cy.get('button[type="submit"]').click();
    cy.wait('@updateSession');

    cy.url().should('include', '/sessions');
  });

});
