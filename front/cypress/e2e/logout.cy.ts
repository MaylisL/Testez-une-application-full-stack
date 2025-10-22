describe('Logout flow', () => {
 let user;

 before(() => {
    cy.fixture('user').then((data) => {
      user = data;
    });
  });

  it('logs out successfully', () => {
    cy.login(user);
    cy.contains('Logout').should('be.visible');
    cy.contains('Logout').click();

    cy.contains('Login').should('be.visible');
    cy.contains('Register').should('be.visible');
    cy.contains('Sessions').should('not.exist');
    cy.contains('Account').should('not.exist');
    cy.contains('Logout').should('not.exist');

    cy.url().should('include', '/');
  });
});
