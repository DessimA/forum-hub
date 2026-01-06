describe('Core Navigation', () => {
  it('should complete a full user journey', () => {
    cy.visit('/login')
    
    // 1. Login
    cy.get('[data-cy="login-email"]').find('input').type('admin@forumhub.com')
    cy.get('[data-cy="login-password"]').find('input').type('admin123')
    cy.get('[data-cy="login-submit"]').click()
    cy.url().should('include', '/topics')

    // 2. Click a topic
    // Wait for content to load
    cy.get('[data-cy="topic-card"]', { timeout: 20000 })
      .should('have.length.at.least', 1)
      .first()
      .scrollIntoView()
      .click({ force: true })
    cy.url().should('include', '/topics/')

    // 3. Verify detail layout
    cy.get('h1', { timeout: 15000 }).should('be.visible') // Title
    cy.viewport(1280, 720)
    
    // 4. Navigate back to Home (authenticated)
    cy.get('[data-cy="navbar-logo"]').click({ force: true })
    cy.url().should('include', '/topics')
  })
})