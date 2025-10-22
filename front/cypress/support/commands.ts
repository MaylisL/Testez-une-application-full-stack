// cypress/support/commands.ts
Cypress.Commands.add('login', (user = {
  id: 1,
  username: 'user1',
  firstName: 'firstname1',
  lastName: 'lastname1',
  email: 'yoga@studio.com',
  admin: false
}, sessions = []) => {
  cy.intercept('POST', 'api/auth/login', { statusCode: 200, body: user }).as('loginUser');

  cy.intercept('GET', '/api/session', sessions).as('getSession');

  cy.visit('/login');
  cy.get('input[formControlName=email]').clear().type('yoga@studio.com');
  cy.get('input[formControlName=password]').clear().type('test!1234');
  cy.get('button[type="submit"]').click();

  cy.wait('@loginUser');
  cy.wait('@getSession');
  cy.url().should('include', '/sessions');
});
