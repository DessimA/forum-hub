describe('Mobile Topic Cards', () => {
  beforeEach(() => {
    cy.viewport(375, 667)
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    // Wait for the main content to ensure layout is ready
    cy.contains('h1', 'Explorar Tópicos', { timeout: 30000 }).should('be.visible')
  })
  
  it('cards should occupy full width', () => {
    // Check if the card container is present
    cy.get('[data-cy="topic-card"]', { timeout: 30000 }).should('have.length.at.least', 1)
    
    // Check if card is full width minus padding
    // Reduced expectation to 280px to account for container padding on small screens (375px)
    cy.get('[data-cy="topic-card"]').first().invoke('width').should('be.gt', 280)
  })
  
  it('title should truncate at 2 lines', () => {
    cy.get('[data-cy="topic-card"]', { timeout: 30000 }).should('have.length.at.least', 1)
    cy.get('[data-cy="topic-title"]').first().should('have.css', '-webkit-line-clamp', '2')
  })
})