describe('Notification System', () => {
  beforeEach(() => {
    // Intercept notifications API
    cy.intercept('GET', '**/api/v1/notifications', {
      statusCode: 200,
      body: [
        {
          id: '1',
          content: 'Nova resposta em seu tópico',
          notificationType: 'NEW_ANSWER',
          isRead: false,
          createdAt: new Date().toISOString(),
          topicId: '123'
        }
      ]
    }).as('getNotifications')

    cy.intercept('GET', '**/api/v1/notifications/unread-count', {
      statusCode: 200,
      body: { count: 1 }
    }).as('getUnreadCount')

    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
  })

  it('should display the correct unread count badge', () => {
    cy.wait('@getUnreadCount')
    cy.get('[data-cy="notification-bell"]').find('.v-badge__badge')
      .should('be.visible')
      .and('contain', '1')
  })

  it('should open notification menu and show content', () => {
    cy.get('[data-cy="notification-bell"]').click()
    cy.contains('Notificações').should('be.visible')
    cy.contains('Nova resposta em seu tópico').should('be.visible')
  })

  it('should navigate to topic when notification is clicked', () => {
    cy.get('[data-cy="notification-bell"]').click()
    
    cy.intercept('GET', '**/api/v1/topics/123', {
      statusCode: 200,
      body: { id: '123', title: 'Test Topic', message: 'Message', status: 'OPEN', author: 'Author', answers: [] }
    }).as('getTopic')

    cy.intercept('PATCH', '**/api/v1/notifications/1/read', {
      statusCode: 200
    }).as('markAsRead')

    cy.contains('Nova resposta em seu tópico').click()
    
    cy.url().should('include', '/topics/123')
    cy.wait('@markAsRead')
  })
})
