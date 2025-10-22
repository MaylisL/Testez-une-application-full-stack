describe('Login Page', () => {

  beforeEach(() => {
    cy.visit('/login');
  });

  it('should login successfully as a user and redirect to /sessions', () => {
    cy.fixture('user').then((user) => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: user,
    }).as('loginUser');

     cy.intercept(
          {
            method: 'GET',
            url: '/api/session',
          },
          []).as('session')

    cy.get('input[formControlName=email]').type(user.email);
    cy.get('input[formControlName=password]').type('password123');

    cy.get('button[type="submit"]').click();
    cy.wait('@loginUser');
    cy.url().should('include', '/sessions');
    });
  });
});
