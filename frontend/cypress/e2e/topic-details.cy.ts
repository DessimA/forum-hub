describe('Topic Details UI', () => {
  beforeEach(() => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    // Wait for topics to load and cards to be rendered
    cy.get('[data-cy="topic-card"]', { timeout: 30000 })
      .should('have.length.at.least', 1)
      .first()
      .scrollIntoView()
      .click({ force: true })
  })

  it('should show the unified reply form', () => {
    cy.contains('Sua Resposta', { timeout: 15000 }).should('be.visible')
    cy.get('textarea[placeholder="Descreva sua contribuição detalhadamente..."]').should('be.visible')
  })

  it('should have responsive reply button', () => {
    // Desktop
    cy.viewport(1280, 720)
    cy.contains('button', 'PUBLICAR RESPOSTA').should('be.visible')

    // Mobile
    cy.viewport(375, 667)
    cy.contains('button', 'PUBLICAR RESPOSTA').should('be.visible')
  })
})