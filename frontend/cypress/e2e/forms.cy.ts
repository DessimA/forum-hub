describe('Form Interactions', () => {
  beforeEach(() => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    // Wait for initial load to ensure we don't click too early
    cy.get('[data-cy="topic-card"]', { timeout: 30000 }).should('have.length.at.least', 1)
  })

  it('should validate and create a new topic with unique title', () => {
    const uniqueTitle = `Topic ${Math.random().toString(36).substring(2, 8)} ${Date.now()}`
    
    cy.get('[data-cy="nav-new-topic"]').should('be.visible').click()
    
    // Wait for dialog animation
    cy.get('.v-dialog', { timeout: 15000 }).should('be.visible')

    // Fill title
    cy.get('input[placeholder="O que você deseja compartilhar ou perguntar?"]')
      .should('be.visible')
      .type(uniqueTitle, { force: true })
    
    // Select course
    cy.get('.v-dialog .v-select').find('.v-field').click({ force: true })
    
    // Wait for the list items to be present in the overlay
    cy.get('.v-overlay-container .v-list-item', { timeout: 15000 })
      .should('have.length.at.least', 1)
      .first()
      .click({ force: true })
    
    // Fill message
    cy.get('textarea[placeholder="Forneça detalhes para que a comunidade possa ajudar..."]')
      .should('be.visible')
      .type('Automated test message description.', { force: true })

    // Submit
    cy.get('[data-cy="submit-topic"]').should('not.be.disabled').click({ force: true })
    
    // Check if dialog closed
    cy.get('.v-dialog', { timeout: 25000 }).should('not.exist')
    
    // Verify our UNIQUE topic appears in the list
    cy.contains(uniqueTitle, { timeout: 25000 }).should('be.visible')
  })

  it('should reply to a topic', () => {
    cy.get('[data-cy="topic-card"]', { timeout: 20000 }).should('be.visible').first().click({ force: true })
    
    // Ensure we are on details page
    cy.get('textarea[placeholder="Sua contribuição..."]', { timeout: 20000 })
      .scrollIntoView()
      .should('be.visible')
      .type('Reply from Cypress ' + Date.now(), { force: true })
    
    cy.contains('button', 'POSTAR RESPOSTA').click({ force: true })
    
    cy.get('.v-card', { timeout: 20000 }).should('contain', 'Reply from Cypress')
  })
})