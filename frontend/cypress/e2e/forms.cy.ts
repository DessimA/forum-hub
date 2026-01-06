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
    cy.get('input[placeholder="Ex: Erro de conexão no Spring Boot"]')
      .should('be.visible')
      .type(uniqueTitle, { force: true })
    
    // Select course
    // Target the specific field within the dialog to ensure click registration
    cy.get('.v-dialog .v-select').find('.v-field').click({ force: true })
    
    // Wait for the list items to be present in the overlay
    cy.get('.v-overlay-container .v-list-item', { timeout: 15000 })
      .should('have.length.at.least', 1)
      .first()
      .click({ force: true })
    
    // Fill message
    cy.get('textarea[placeholder="Descreva sua dúvida ou problema com detalhes..."]')
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
    // Wait for content to load
    cy.get('[data-cy="topic-card"]', { timeout: 20000 }).should('be.visible').first().click({ force: true })
    
    // Ensure we are on details page
    cy.get('textarea[placeholder="Descreva sua contribuição detalhadamente..."]', { timeout: 20000 })
      .should('be.visible')
      .type('Reply from Cypress ' + Date.now(), { force: true })
    
    cy.contains('button', 'PUBLICAR RESPOSTA').click({ force: true })
    
    // Wait for the new reply to appear
    cy.contains('p', 'pelo menos um caractere', { timeout: 5000 }).should('not.exist')
    cy.get('.v-card', { timeout: 20000 }).should('contain', 'Reply from Cypress')
  })
})