describe('Me Page', () => {
  const user = {
    id: 1,
    username: 'user1',
    firstName: 'firstname1',
    lastName: 'lastname1',
    email: 'user1@demo.com',
    admin: false,
    createdAt: '2024-07-10T00:00:00Z',
    updatedAt: '2025-03-05T00:00:00Z'
  };

  const admin = {
    id: 2,
    username: 'admin1',
    firstName: 'adminFirst',
    lastName: 'adminLast',
    email: 'admin@demo.com',
    admin: true,
    createdAt: '2024-07-10T00:00:00Z',
    updatedAt: '2025-03-05T00:00:00Z'
  };



  it('shows user info as a normal user', () => {
    cy.login(user);

    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: user
    }).as('getUser');

    cy.get('[routerLink="me"]').click();
    cy.url().should('include', '/me');
    cy.wait('@getUser');

    cy.contains(`Name: ${user.firstName} ${user.lastName.toUpperCase()}`).should('be.visible');
    cy.contains(`Email: ${user.email}`).should('be.visible');

    cy.contains('Delete my account:').should('be.visible');
    cy.get('button[mat-raised-button][color="warn"]').within(() => {
      cy.get('mat-icon').should('contain.text', 'delete');
      cy.contains('Detail').should('be.visible');
    });
    const createdDate = new Date(user.createdAt).toLocaleDateString('en-US', { dateStyle: 'long' });
    const updatedDate = new Date(user.updatedAt).toLocaleDateString('en-US', { dateStyle: 'long' });
    cy.contains(createdDate).should('be.visible');
    cy.contains(updatedDate).should('be.visible');

    cy.contains('You are admin').should('not.exist');
  });

  it('shows user info as an admin', () => {
    cy.login(admin);

    cy.intercept('GET', '/api/user/2', {
      statusCode: 200,
      body: admin
    }).as('getAdmin');

    cy.get('[routerLink="me"]').click();
    cy.url().should('include', '/me');
    cy.wait('@getAdmin');

    cy.contains(`Name: ${admin.firstName} ${admin.lastName.toUpperCase()}`).should('be.visible');
    cy.contains(`Email: ${admin.email}`).should('be.visible');
    cy.contains('You are admin').should('be.visible');

    cy.contains('Delete my account:').should('not.exist');
    cy.get('button[mat-raised-button][color="warn"]').should('not.exist');

    const createdDate = new Date(admin.createdAt).toLocaleDateString('en-US', { dateStyle: 'long' });
    const updatedDate = new Date(admin.updatedAt).toLocaleDateString('en-US', { dateStyle: 'long' });
    cy.contains(createdDate).should('be.visible');
    cy.contains(updatedDate).should('be.visible');
  });
});
