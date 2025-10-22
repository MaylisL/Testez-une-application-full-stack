describe('Register Page', () => {

  beforeEach(() => {
    cy.visit('/register');
  });

  it('should register successfully and redirect to /login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('register');

    cy.intercept('GET', '/api/sessions', {
      statusCode: 200,
      body: []
    }).as('sessions');

   cy.get('input[formControlName="firstName"]').type('testprenomyoga');
   cy.get('input[formControlName="lastName"]').type('testnomfamilleyoga');
   cy.get('input[formControlName="email"]').type('pnyoga@demo.com');
   cy.get('input[formControlName="password"]').type('testyoga1234');
   cy.get('button[type="submit"]').click();

   cy.wait('@register');
   cy.url().should('include', '/login');
  });

  it('should show an error when user already exists', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 409,
      body: { message: 'User already exists' }
    }).as('registerError');

    cy.get('input[formControlName=firstName]').type('testprenomyoga');
    cy.get('input[formControlName=lastName]').type('testnomfamilleyoga');
    cy.get('input[formControlName=email]').type('pnyoga@demo.com');
    cy.get('input[formControlName=password]').type('testyoga1234');

    cy.get('button[type="submit"]').click();

    cy.wait('@registerError');

    cy.get('.error')
      .should('be.visible')
      .and('contain', 'An error occurred');
  });

  it('should show an error when server fails', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 500,
      body: { message: 'Internal Server Error' }
    }).as('registerServerError');

    cy.get('input[formControlName=firstName]').type('testprenomyoga');
    cy.get('input[formControlName=lastName]').type('testnomfamilleyoga');
    cy.get('input[formControlName=email]').type('pnyoga@demo.com');
    cy.get('input[formControlName=password]').type('testyoga1234');

    cy.get('button[type="submit"]').click();

    cy.wait('@registerServerError');

    cy.get('.error')
      .should('be.visible')
      .and('contain', 'An error occurred');
  });
   it('should keep the submit button disabled if form is invalid', () => {
      cy.get('button[type="submit"]').should('be.disabled');

      cy.get('input[formControlName="email"]').type('not-an-email');
      cy.get('button[type="submit"]').should('be.disabled');

      cy.get('input[formControlName="password"]').type('short');
      cy.get('button[type="submit"]').should('be.disabled');
    });
});
