document.addEventListener('DOMContentLoaded', () => {

    const API_URL = 'http://localhost:8081/api'; // Your API URL

    const loggedInUserId = sessionStorage.getItem('pgUserId');
    const loggedInUserName = sessionStorage.getItem('pgUserName');
    const loggedInUserRole = sessionStorage.getItem('pgUserRole');

    document.getElementById('logout-btn')?.addEventListener('click', logout);
    document.querySelector('.btn-logout-sidebar')?.addEventListener('click', logout);

    function logout(e) {
        e.preventDefault();
        sessionStorage.clear();
        alert('You have been logged out.');
        window.location.href = 'index.html';
    }


    if (document.body.classList.contains('auth-page')) {
        
        window.switchLogin = function(role) {
            if (role === 'owner') {
                document.getElementById('tenant-login').style.display = 'none';
                document.getElementById('owner-login').style.display = 'flex';
            } else {
                document.getElementById('tenant-login').style.display = 'flex';
                document.getElementById('owner-login').style.display = 'none';
            }
        };
        window.switchSignup = function(role) {
            if (role === 'owner') {
                document.getElementById('tenant-signup').style.display = 'none';
                document.getElementById('owner-signup').style.display = 'flex';
            } else {
                document.getElementById('tenant-signup').style.display = 'flex';
                document.getElementById('owner-signup').style.display = 'none';
            }
        };

        async function handleSignup(role) {
            const name = document.getElementById(role === 'tenant' ? 'tenant-signup-name' : 'owner-signup-name').value;
            const email = document.getElementById(role === 'tenant' ? 'tenant-signup-email' : 'owner-signup-email').value;
            const password = document.getElementById(role === 'tenant' ? 'password-tenant' : 'password-owner').value;
            const errorDiv = document.getElementById(role === 'tenant' ? 'error-tenant' : 'error-owner');

            try {
                const response = await fetch(`${API_URL}/auth/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, password, role })
                });
                if (response.ok) {
                    alert('Registration Successful! Please log in.');
                    window.location.href = 'login.html';
                } else {
                    errorDiv.textContent = await response.text();
                    errorDiv.style.display = 'block';
                }
            } catch (error) {
                errorDiv.textContent = 'Could not connect to the server.';
                errorDiv.style.display = 'block';
            }
        }
        
        const signupFormTenant = document.getElementById('signup-form-tenant');
        if (signupFormTenant) {
            signupFormTenant.addEventListener('submit', (e) => { e.preventDefault(); handleSignup('tenant'); });
        }
        const signupFormOwner = document.getElementById('signup-form-owner');
        if (signupFormOwner) {
            signupFormOwner.addEventListener('submit', (e) => { e.preventDefault(); handleSignup('owner'); });
        }

        async function handleLogin(role) {
            const email = document.getElementById(role === 'tenant' ? 'tenant-email' : 'owner-email').value;
            const password = document.getElementById(role === 'tenant' ? 'tenant-password' : 'owner-password').value;

            try {
                const response = await fetch(`${API_URL}/auth/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });
                
                if (response.ok) {
                    const data = await response.json(); 
                    
                    sessionStorage.setItem('pgUserRole', data.role);
                    sessionStorage.setItem('pgUserId', data.userId);
                    sessionStorage.setItem('pgUserName', data.name);
                    
                    alert('Login Successful! Welcome, ' + data.name);
                    
                    window.location.href = (data.role === 'tenant') ? 'dashboard.html' : 'owner-dashboard.html';
                
                } else {
                    alert('Login Failed: Invalid email or password.');
                }
            } catch (error) {
                alert('Error: Could not connect to the server.');
            }
        }

        const loginFormTenant = document.getElementById('login-form-tenant');
        if (loginFormTenant) {
            loginFormTenant.addEventListener('submit', (e) => { e.preventDefault(); handleLogin('tenant'); });
        }
        const loginFormOwner = document.getElementById('login-form-owner');
        if (loginFormOwner) {
             loginFormOwner.addEventListener('submit', (e) => { e.preventDefault(); handleLogin('owner'); });
        }
    }
    
    if (document.querySelector('.dashboard-main-content')) {
        
        if (loggedInUserName) {
            document.getElementById('tenant-welcome-name').textContent = `Welcome, ${loggedInUserName}`;
        } else {
            document.getElementById('tenant-welcome-name').textContent = `Welcome, Guest`;
        }
        
        const pgGrid = document.getElementById('pg-listings-grid');
        const resultsCount = document.getElementById('results-count');
        const filterForm = document.getElementById('pg-filter-form');
        let allPgs = [];

        const modal = document.getElementById('pg-modal');
        const modalCloseBtn = document.getElementById('modal-close-btn');
        const modalBookBtn = document.getElementById('modal-book-btn');
        const modalFeedbackBtn = document.getElementById('modal-feedback-btn');
        let currentPgForModal = null;

        const tenantViews = document.querySelectorAll('.view');
        const tenantNavButtons = document.querySelectorAll('.main-actions .action-btn');
        const myBookingsTableBody = document.getElementById('my-bookings-table-body');
        const noTenantBookingsMessage = document.getElementById('no-tenant-bookings-message');
        const paymentHistoryTableBody = document.getElementById('payment-history-table-body');
        const noPaymentHistoryMessage = document.getElementById('no-payment-history-message');
        const notificationBell = document.getElementById('notification-bell-icon');
        const notificationsTableBody = document.getElementById('notifications-table-body');
        const noNotificationsMessage = document.getElementById('no-notifications-message');

        function showTenantView(viewId) {
            tenantViews.forEach(view => view.classList.remove('active'));
            document.getElementById(viewId).classList.add('active');
            
            tenantNavButtons.forEach(btn => {
                btn.classList.toggle('active', btn.dataset.view === viewId);
            });

            if (viewId === 'view-find-pg') fetchAndDisplayPGs();
            if (viewId === 'view-my-bookings') fetchTenantBookings();
            if (viewId === 'view-payment-history') fetchTenantPaymentHistory();
            if (viewId === 'view-notifications') fetchTenantNotifications(); // <-- NEW
        }

        tenantNavButtons.forEach(btn => {
            btn.addEventListener('click', () => showTenantView(btn.dataset.view));
        });
        
        notificationBell.addEventListener('click', () => showTenantView('view-notifications'));

        async function fetchAndDisplayPGs() {
            try {
                const response = await fetch(`${API_URL}/pgs`);
                allPgs = await response.json();
                applyFilters();
            } catch (error) {
                resultsCount.textContent = 'Error loading data from the server.';
            }
        }

        async function fetchTenantBookings() {
            if (!loggedInUserId) {
                noTenantBookingsMessage.textContent = 'Please log in to see your bookings.';
                noTenantBookingsMessage.style.display = 'block';
                return;
            }
            
            const response = await fetch(`${API_URL}/bookings/tenant/${loggedInUserId}`);
            myBookingsTableBody.innerHTML = '';

            if (response.status === 204) {
                noTenantBookingsMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noTenantBookingsMessage.style.display = 'block';
                noTenantBookingsMessage.textContent = 'Error loading bookings.';
                return;
            }

            const bookings = await response.json();
            noTenantBookingsMessage.style.display = 'none';

            bookings.forEach(booking => {
                const row = document.createElement('tr');
                let contactInfo = 'N/A (Pending Approval)';
                
                if(booking.status === 'Approved') {
                    contactInfo = `
                        <div class="owner-contact-info">
                            <span>${booking.ownerName}</span>
                            <span>${booking.contactPhone}</span>
                            <span>${booking.contactEmail}</span>
                        </div>
                    `;
                }

                row.innerHTML = `
                    <td>${booking.pgName}</td>
                    <td class="status-${booking.status.toLowerCase()}">${booking.status}</td>
                    <td>${new Date(booking.requestDate).toLocaleDateString()}</td>
                    <td>${booking.ownerName}</td>
                    <td>${contactInfo}</td>
                `;
                myBookingsTableBody.appendChild(row);
            });
        }
        
        async function fetchTenantPaymentHistory() {
            if (!loggedInUserId) {
                noPaymentHistoryMessage.textContent = 'Please log in to see your payment history.';
                noPaymentHistoryMessage.style.display = 'block';
                return;
            }
            
            const response = await fetch(`${API_URL}/payments/tenant/${loggedInUserId}`);
            paymentHistoryTableBody.innerHTML = '';

            if (response.status === 204) {
                noPaymentHistoryMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noPaymentHistoryMessage.style.display = 'block';
                noPaymentHistoryMessage.textContent = 'Error loading payment history.';
                return;
            }

            const payments = await response.json();
            noPaymentHistoryMessage.style.display = 'none';

            payments.forEach(pay => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${new Date(pay.paymentDate).toLocaleDateString()}</td>
                    <td>${pay.pgName}</td>
                    <td>₹${pay.amount.toLocaleString('en-IN')}</td>
                    <td>${pay.paymentForMonth}</td>
                    <td class="status-${pay.status.toLowerCase()}">${pay.status}</td>
                `;
                paymentHistoryTableBody.appendChild(row);
            });
        }

        async function fetchTenantNotifications() {
            if (!loggedInUserId) {
                noNotificationsMessage.textContent = 'Please log in to see your notifications.';
                noNotificationsMessage.style.display = 'block';
                return;
            }
            
            const response = await fetch(`${API_URL}/notifications/tenant/${loggedInUserId}`);
            notificationsTableBody.innerHTML = '';

            if (response.status === 204) {
                noNotificationsMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noNotificationsMessage.style.display = 'block';
                noNotificationsMessage.textContent = 'Error loading notifications.';
                return;
            }

            const notifications = await response.json();
            noNotificationsMessage.style.display = 'none';

            notifications.forEach(n => {
                const row = document.createElement('tr');
                let actionButton = 'Read';
                if (n.status === 'Sent') {
                    actionButton = `<button class="btn-action-read" data-id="${n.notificationId}">Mark as Read</button>`;
                }

                row.innerHTML = `
                    <td>${new Date(n.createdAt).toLocaleDateString()}</td>
                    <td>${n.pgName}</td>
                    <td>${n.message}</td>
                    <td class="status-${n.status.toLowerCase()}">${n.status}</td>
                    <td>${actionButton}</td>
                `;
                notificationsTableBody.appendChild(row);
            });
        }
        
        notificationsTableBody.addEventListener('click', async (e) => {
            if (e.target.classList.contains('btn-action-read')) {
                const button = e.target;
                const notificationId = button.dataset.id;
                
                try {
                    const response = await fetch(`${API_URL}/notifications/${notificationId}/read`, {
                        method: 'PUT'
                    });
                    if (response.ok) {
                        fetchTenantNotifications(); 
                    } else {
                        throw new Error(await response.text());
                    }
                } catch (error) {
                    alert(`Error: ${error.message}`);
                }
            }
        });


        function displayPGs(pgs) {
            pgGrid.innerHTML = '';
            resultsCount.textContent = `Displaying ${pgs.length} PGs...`;
            pgs.forEach(pg => {
                const card = document.createElement('div');
                card.className = 'pg-card';
                card.dataset.pgId = pg.pgId; 
                
                card.innerHTML = `
                    <div class="pg-card-image" style="background-image: url('${pg.image}')"></div>
                    <div class="pg-card-content">
                        <h3 class="pg-card-title">${pg.pgName}</h3>
                        <p class="pg-card-location"><i class="fas fa-map-marker-alt"></i> ${pg.city}</p>
                        
                        <div class="pg-card-contact-wrapper">
                            <p class="pg-card-contact"><i class="fas fa-phone-alt"></i> ${pg.contactPhone}</p>
                            <p class="pg-card-contact"><i class="fas fa-envelope"></i> ${pg.contactEmail}</p>
                        </div>
                        
                        <p class="pg-card-price">₹${pg.rent.toLocaleString('en-IN')}<small>/month</small></p>
                    </div>`;
                
                card.addEventListener('click', () => openPgModal(pg.pgId));
                pgGrid.appendChild(card);
            });
        }
        
        function applyFilters() {
            const searchTerm = document.getElementById('search-name-location').value.toLowerCase();
            const maxRent = parseInt(document.getElementById('max-rent').value, 10);
            const location = document.getElementById('location').value;
            const pgType = document.getElementById('pg-type').value;
            const roomType = document.getElementById('room-type').value;
            
            const filteredPgs = allPgs.filter(pg => 
                (searchTerm === '' || pg.pgName.toLowerCase().includes(searchTerm) || pg.city.toLowerCase().includes(searchTerm)) &&
                (isNaN(maxRent) || !maxRent || pg.rent <= maxRent) &&
                (location === 'all' || pg.city === location) &&
                (pgType === 'all' || pg.pgType === pgType) &&
                (roomType === 'all' || pg.roomType === roomType)
            );
            displayPGs(filteredPgs);
        }

        async function openPgModal(pgId) {
            try {
                const response = await fetch(`${API_URL}/pgs/${pgId}`);
                if (!response.ok) throw new Error('Could not fetch PG details.');
                
                const pg = await response.json();
                currentPgForModal = pg; 

                document.getElementById('modal-pg-name').textContent = pg.pgName;
                document.getElementById('modal-pg-image').style.backgroundImage = `url('${pg.image}')`;
                document.getElementById('modal-pg-city').textContent = pg.city;
                document.getElementById('modal-pg-address').textContent = pg.address;
                document.getElementById('modal-pg-type').textContent = pg.pgType;
                document.getElementById('modal-pg-rent').textContent = `₹${pg.rent.toLocaleString('en-IN')} / month`;
                document.getElementById('modal-pg-description').textContent = pg.description || 'No description provided.';
                document.getElementById('modal-pg-rules').textContent = pg.rules || 'No rules provided.';

                const amenitiesList = document.getElementById('modal-pg-amenities');
                amenitiesList.innerHTML = '';
                if (pg.amenities && pg.amenities.length > 0) {
                    pg.amenities.forEach(amenity => {
                        const li = document.createElement('li');
                        li.textContent = amenity.amenityName;
                        amenitiesList.appendChild(li);
                    });
                } else {
                    amenitiesList.innerHTML = '<li>No amenities listed.</li>';
                }
                
                modal.style.display = 'flex';
            } catch (error) {
                console.error(error);
                alert('Error loading PG details.');
            }
        }

        function closePgModal() {
            modal.style.display = 'none';
            currentPgForModal = null;
        }

        async function handleBookingRequest() {
            if (!loggedInUserId || loggedInUserRole !== 'tenant') {
                alert('Please log in as a tenant to book a PG.');
                return;
            }
            if (!currentPgForModal) return;

            const bookingRequest = {
                pgId: currentPgForModal.pgId,
                tenantId: parseInt(loggedInUserId, 10),
                ownerId: currentPgForModal.ownerId
            };

            try {
                const response = await fetch(`${API_URL}/bookings`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(bookingRequest)
                });
                if (response.ok) {
                    alert('Booking request sent successfully!');
                    closePgModal();
                    fetchTenantBookings(); 
                } else {
                    throw new Error(await response.text());
                }
            } catch (error) {
                console.error(error);
                alert(`Error: ${error.message}`);
            }
        }
        
        async function handleFeedbackRequest() {
            if (!loggedInUserId || loggedInUserRole !== 'tenant') {
                alert('Please log in as a tenant to give feedback.');
                return;
            }
            if (!currentPgForModal) return;

            const rating = prompt("Please provide a rating (1-5):");
            if (!rating || !['1','2','3','4','5'].includes(rating)) {
                alert("Invalid rating. Please enter a number between 1 and 5.");
                return;
            }
            const comment = prompt("Please provide your feedback (optional):");

            const feedbackData = {
                pgId: currentPgForModal.pgId,
                tenantId: parseInt(loggedInUserId, 10),
                ownerId: currentPgForModal.ownerId,
                rating: parseInt(rating, 10),
                comment: comment
            };

             try {
                const response = await fetch(`${API_URL}/feedbacks`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(feedbackData)
                });
                if(response.ok) {
                    alert('Feedback submitted successfully!');
                    closePgModal();
                } else {
                    throw new Error(await response.text());
                }
            } catch (error) {
                console.error(error);
                alert(`Error: ${error.message}`);
            }
        }

        filterForm.addEventListener('input', applyFilters);
        modalCloseBtn.addEventListener('click', closePgModal);
        modalBookBtn.addEventListener('click', handleBookingRequest);
        modalFeedbackBtn.addEventListener('click', handleFeedbackRequest);

        fetchAndDisplayPGs(); 
    }
    
    if (document.body.classList.contains('owner-dashboard-page')) {
        
        if (loggedInUserName && loggedInUserRole === 'owner') {
            document.querySelector('.user-profile span').textContent = `Welcome, ${loggedInUserName}`;
        } else {
            alert('You are not logged in or not an owner.');
            window.location.href = 'login.html';
            return;
        }

        const mainTitle = document.getElementById('main-title');
        const views = document.querySelectorAll('.view');
        
        function showView(viewId) {
            views.forEach(view => view.classList.remove('active'));
            document.getElementById(viewId).classList.add('active');
            
            const activeLink = document.querySelector(`.nav-link[data-view="${viewId}"]`);
            document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
            
            if (activeLink) {
                activeLink.classList.add('active');
                mainTitle.textContent = activeLink.textContent.trim();
            }

            if (viewId === 'view-dashboard') fetchDashboardStats();
            if (viewId === 'view-listings') fetchAndRenderOwnerPgs();
            if (viewId === 'view-bookings') fetchBookingRequests();
            if (viewId === 'view-manage-rent') fetchActiveTenants();
            if (viewId === 'view-owner-payments') fetchOwnerPaymentHistory();
            if (viewId === 'view-feedback') fetchFeedback();
            if (viewId === 'view-notifications') fetchSentNotifications();
            if (viewId === 'view-add-pg' && !editPgIdInput.value) fetchAmenitiesForForm(); 
            if (viewId === 'view-manage-rooms') fetchAndRenderOwnerPgsForRoomManagement(); 
        }

        document.querySelector('.sidebar-nav').addEventListener('click', (e) => {
            const link = e.target.closest('.nav-link');
            if (link && link.dataset.view) { e.preventDefault(); showView(link.dataset.view); }
        });
        document.querySelector('.quick-actions').addEventListener('click', (e) => {
            const button = e.target.closest('button[data-view]');
            if (button) { showView(button.dataset.view); }
        });

        const statTotalPgs = document.getElementById('stat-total-pgs');
        const statPendingBookings = document.getElementById('stat-pending-bookings');

        const addPgForm = document.getElementById('add-pg-form');
        const myPgListingsDiv = document.getElementById('my-pg-listings');
        const editPgIdInput = document.getElementById('edit-pg-id');
        const formTitle = document.getElementById('form-title');
        const formSubmitBtn = document.getElementById('form-submit-btn');
        const formCancelBtn = document.getElementById('form-cancel-btn');
        const amenitiesContainer = document.getElementById('amenities-container');

        const bookingsTableBody = document.getElementById('bookings-table-body');
        const noBookingsMessage = document.getElementById('no-bookings-message');

        const manageRentTableBody = document.getElementById('manage-rent-table-body');
        const noActiveTenantsMessage = document.getElementById('no-active-tenants-message');
        const ownerPaymentsTableBody = document.getElementById('owner-payments-table-body');
        const noOwnerPaymentsMessage = document.getElementById('no-owner-payments-message');

        const feedbackTableBody = document.getElementById('feedback-table-body');
        const noFeedbackMessage = document.getElementById('no-feedback-message');
        
        const notificationsTableBody = document.getElementById('notifications-table-body');
        const noNotificationsMessage = document.getElementById('no-notifications-message');

        const pgSelectForRooms = document.getElementById('pg-select-for-rooms');
        const roomManagementSection = document.getElementById('room-management-section');
        const addRoomForm = document.getElementById('add-room-form');
        const pgIdForNewRoomInput = document.getElementById('pg-id-for-new-room');
        const roomsListTableBody = document.getElementById('rooms-list-table-body');

        const roomApprovalModal = document.getElementById('room-approval-modal');
        const modalBookingIdInput = document.getElementById('modal-booking-id');
        const modalRoomSelect = document.getElementById('modal-room-select');
        const modalConfirmApproveBtn = document.getElementById('modal-confirm-approve');
        const modalCancelApproveBtn = document.getElementById('modal-cancel-approve');
        const modalCloseApproveBtn = document.getElementById('modal-close-approve');


        async function fetchDashboardStats() {
            try {
                const pgsResponse = await fetch(`${API_URL}/pgs/owner/${loggedInUserId}`);
                let pgs = [];
                if (pgsResponse.status === 200) pgs = await pgsResponse.json();
                statTotalPgs.textContent = pgs.length;
            } catch (e) { console.error(e); }
            
            try {
                const bookingsResponse = await fetch(`${API_URL}/bookings/owner/${loggedInUserId}`);
                let bookings = [];
                if (bookingsResponse.status === 200) bookings = await bookingsResponse.json();
                const pendingBookings = bookings.filter(b => b.status === 'Pending').length;
                statPendingBookings.textContent = pendingBookings;
            } catch (e) { console.error(e); }
        }
        
        async function fetchAndRenderOwnerPgs() {
            const response = await fetch(`${API_URL}/pgs/owner/${loggedInUserId}`);
            let pgs = [];
            
            if (response.status === 204) pgs = [];
            else if (response.ok) pgs = await response.json();
            else console.error("Failed to fetch PGs");
            
            myPgListingsDiv.innerHTML = '';
            document.getElementById('no-pgs-listed').style.display = pgs.length === 0 ? 'block' : 'none';
            
            pgs.forEach(pg => {
                const card = document.createElement('div');
                card.className = 'pg-card';
                card.innerHTML = `
                    <div class="owner-actions">
                        <button class="btn-action btn-edit" data-id="${pg.pgId}"><i class="fas fa-pen"></i></button>
                        <button class="btn-action btn-delete" data-id="${pg.pgId}"><i class="fas fa-trash"></i></button>
                    </div>
                    <div class="pg-card-image" style="background-image: url('${pg.image}')"></div>
                    <div class="pg-card-content">
                        <h3 class="pg-card-title">${pg.pgName}</h3>
                        <p class="pg-card-location"><i class="fas fa-map-marker-alt"></i> ${pg.city}</p>
                    </div>`;
                myPgListingsDiv.appendChild(card);
            });
        }
        
        async function fetchBookingRequests() {
            const response = await fetch(`${API_URL}/bookings/owner/${loggedInUserId}`);
            bookingsTableBody.innerHTML = '';
            
            if (response.status === 204) {
                noBookingsMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noBookingsMessage.style.display = 'block';
                noBookingsMessage.textContent = 'Error loading requests.';
                return;
            }

            const bookings = await response.json();
            noBookingsMessage.style.display = 'none';

            bookings.forEach(booking => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${booking.pgName}</td>
                    <td>${booking.tenantName}</td>
                    <td>${booking.tenantEmail}</td>
                    <td>${new Date(booking.requestDate).toLocaleDateString()}</td>
                    <td class="status-${booking.status.toLowerCase()}">${booking.status}</td>
                    <td class="booking-actions">
                        ${booking.status === 'Pending' ? `
                            <button class="btn-action-approve" data-id="${booking.bookingId}" data-pg-id="${booking.pgId}">Approve</button>
                            <button class="btn-action-reject" data-id="${booking.bookingId}">Reject</button>
                        ` : 'Managed'}
                    </td>
                `;
                bookingsTableBody.appendChild(row);
            });
        }

        async function fetchActiveTenants() {
            const response = await fetch(`${API_URL}/tenancies/owner/${loggedInUserId}`);
            manageRentTableBody.innerHTML = '';

            if (response.status === 204) {
                noActiveTenantsMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noActiveTenantsMessage.style.display = 'block';
                noActiveTenantsMessage.textContent = 'Error loading tenants.';
                return;
            }

            const tenants = await response.json();
            noActiveTenantsMessage.style.display = 'none';
            
            tenants.forEach(tenant => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${tenant.tenantName}</td>
                    <td>${tenant.pgName}</td>
                    <td>${tenant.tenantEmail}</td>
                    <td>₹${tenant.rentPerMonth.toLocaleString('en-IN')}</td>
                    <td class="rent-actions">
                        <button class="btn-action-paid" 
                            data-tenancy-id="${tenant.tenancyId}" 
                            data-amount="${tenant.rentPerMonth}"
                        >Mark as Paid</button>
                        <button class="btn-action-remind" 
                            data-tenant-id="${tenant.tenantId}"
                            data-pg-id="${tenant.pgId}"
                        >Send Reminder</button>
                    </td>
                `;
                manageRentTableBody.appendChild(row);
            });
        }
        
        async function fetchOwnerPaymentHistory() {
            const response = await fetch(`${API_URL}/payments/owner/${loggedInUserId}`);
            ownerPaymentsTableBody.innerHTML = '';

            if (response.status === 204) {
                noOwnerPaymentsMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noOwnerPaymentsMessage.style.display = 'block';
                noOwnerPaymentsMessage.textContent = 'Error loading payment history.';
                return;
            }

            const payments = await response.json();
            noOwnerPaymentsMessage.style.display = 'none';

            payments.forEach(pay => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${new Date(pay.paymentDate).toLocaleDateString()}</td>
                    <td>${pay.tenantName}</td>
                    <td>${pay.pgName}</td>
                    <td>₹${pay.amount.toLocaleString('en-IN')}</td>
                    <td>${pay.paymentForMonth}</td>
                    <td class="status-${pay.status.toLowerCase()}">${pay.status}</td>
                `;
                ownerPaymentsTableBody.appendChild(row);
            });
        }
        
        async function fetchFeedback() {
            const response = await fetch(`${API_URL}/feedbacks/owner/${loggedInUserId}`);
            feedbackTableBody.innerHTML = '';

            if (response.status === 204) {
                noFeedbackMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noFeedbackMessage.style.display = 'block';
                noFeedbackMessage.textContent = 'Error loading feedback.';
                return;
            }
            
            const feedbacks = await response.json();
            noFeedbackMessage.style.display = 'none';
            
            feedbacks.forEach(fb => {
                const row = document.createElement('tr');
                const stars = '★'.repeat(fb.rating) + '☆'.repeat(5 - fb.rating);
                row.innerHTML = `
                    <td>${fb.pgName}</td>
                    <td>${fb.tenantName}</td>
                    <td>${new Date(fb.createdAt).toLocaleDateString()}</td>
                    <td class="stars">${stars}</td>
                    <td>${fb.comment}</td>
                `;
                feedbackTableBody.appendChild(row);
            });
        }
        
        async function fetchSentNotifications() {
             const response = await fetch(`${API_URL}/notifications/owner/${loggedInUserId}`);
            notificationsTableBody.innerHTML = '';

            if (response.status === 204) {
                noNotificationsMessage.style.display = 'block';
                return;
            }
            if (!response.ok) {
                noNotificationsMessage.style.display = 'block';
                noNotificationsMessage.textContent = 'Error loading notifications.';
                return;
            }
            
            const notifications = await response.json();
            noNotificationsMessage.style.display = 'none';
            
            notifications.forEach(n => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${n.tenantName}</td>
                    <td>${n.pgName}</td>
                    <td>${n.message}</td>
                    <td>${new Date(n.createdAt).toLocaleString()}</td>
                    <td class="status-${n.status.toLowerCase()}">${n.status}</td>
                `;
                notificationsTableBody.appendChild(row);
            });
        }

        async function fetchAmenitiesForForm() {
            try {
                const response = await fetch(`${API_URL}/amenities`);
                if (!response.ok) throw new Error('Could not fetch amenities');
                const amenities = await response.json();
                
                amenitiesContainer.innerHTML = '';
                amenities.forEach(amenity => {
                    const div = document.createElement('div');
                    div.className = 'amenity-checkbox';
                    div.innerHTML = `
                        <input type="checkbox" id="amenity-${amenity.amenityId}" name="amenities" value="${amenity.amenityId}">
                        <label for="amenity-${amenity.amenityId}">${amenity.amenityName}</label>
                    `;
                    amenitiesContainer.appendChild(div);
                });
            } catch (error) {
                amenitiesContainer.innerHTML = `<p style="color: red;">${error.message}</p>`;
            }
        }
        
        
        async function fetchAndRenderOwnerPgsForRoomManagement() {
            const response = await fetch(`${API_URL}/pgs/owner/${loggedInUserId}`);
            pgSelectForRooms.innerHTML = '<option value="">-- Select a PG --</option>'; // Reset
            roomManagementSection.style.display = 'none';
            roomsListTableBody.innerHTML = '';

            if (response.status === 204) return; // No PGs
            if (!response.ok) return;

            const pgs = await response.json();
            pgs.forEach(pg => {
                const option = document.createElement('option');
                option.value = pg.pgId;
                option.textContent = pg.pgName;
                pgSelectForRooms.appendChild(option);
            });
        }

        async function fetchRoomsForPg(pgId) {
            if (!pgId) {
                roomManagementSection.style.display = 'none';
                return;
            }
            
            pgIdForNewRoomInput.value = pgId; 
            roomManagementSection.style.display = 'block';
            roomsListTableBody.innerHTML = ''; 

            const response = await fetch(`${API_URL}/rooms/pg/${pgId}`);
            if (response.status === 204) {
                roomsListTableBody.innerHTML = '<tr><td colspan="3" style="text-align:center;">No rooms added for this PG yet.</td></tr>';
                return;
            }
            if (!response.ok) {
                roomsListTableBody.innerHTML = '<tr><td colspan="3" style="color:red;">Error loading rooms.</td></tr>';
                return;
            }

            const rooms = await response.json();
            rooms.forEach(room => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${room.roomNumber}</td>
                    <td>₹${room.rent.toLocaleString('en-IN')}</td>
                    <td class="status-${room.status.toLowerCase()}">${room.status}</td>
                `;
                roomsListTableBody.appendChild(row);
            });
        }
        
        async function handleCreateRoom(e) {
            e.preventDefault();
            const pgId = pgIdForNewRoomInput.value;
            const roomNumber = document.getElementById('room-number').value;
            const rent = document.getElementById('room-rent').value;

            if (!pgId || !roomNumber || !rent) {
                alert('Please fill out all room details.');
                return;
            }
            
            try {
                const response = await fetch(`${API_URL}/rooms`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        pgId: parseInt(pgId, 10),
                        roomNumber: roomNumber,
                        rent: parseFloat(rent)
                    })
                });
                
                if (response.ok) {
                    alert('Room created successfully!');
                    addRoomForm.reset();
                    fetchRoomsForPg(pgId); 
                } else {
                    throw new Error(await response.text());
                }
            } catch (error) {
                alert(`Error creating room: ${error.message}`);
            }
        }
        
        
        function resetForm() {
            addPgForm.reset();
            editPgIdInput.value = '';
            formTitle.textContent = 'Add a New PG';
            formSubmitBtn.textContent = 'Add Listing';
            formCancelBtn.style.display = 'none';
            document.getElementById('pg-image').required = true;
            document.querySelectorAll('input[name="amenities"]').forEach(cb => cb.checked = false);
        }
        
        async function populateFormForEdit(pgId) {
            await fetchAmenitiesForForm(); 
            
            const response = await fetch(`${API_URL}/pgs/${pgId}`); 
            if (!response.ok) {
                alert('Error fetching PG details for editing.');
                return;
            }
            const pgToEdit = await response.json();
            
            document.getElementById('pg-name').value = pgToEdit.pgName;
            document.getElementById('pg-location').value = pgToEdit.city;
            document.getElementById('pg-address').value = pgToEdit.address || '';
            document.getElementById('owner-pg-type').value = pgToEdit.pgType;
            document.getElementById('owner-room-type').value = pgToEdit.roomType; 
            document.getElementById('pg-rent').value = pgToEdit.rent;
            document.getElementById('pg-contact-phone').value = pgToEdit.contactPhone;
            document.getElementById('pg-contact-email').value = pgToEdit.contactEmail;
            document.getElementById('pg-description').value = pgToEdit.description || '';
            document.getElementById('pg-rules').value = pgToEdit.rules || '';
            
            document.querySelectorAll('input[name="amenities"]').forEach(cb => {
                cb.checked = pgToEdit.amenities.some(a => a.amenityId == cb.value);
            });

            editPgIdInput.value = pgToEdit.pgId;
            formTitle.textContent = 'Edit PG Details';
            formSubmitBtn.textContent = 'Update Listing';
            formCancelBtn.style.display = 'inline-block';
            document.getElementById('pg-image').required = false;
            showView('view-add-pg');
        }

        addPgForm.addEventListener('submit', (e) => {
            e.preventDefault();
            
            const editId = parseInt(editPgIdInput.value, 10);
            const imageFile = document.getElementById('pg-image').files[0];
            
            const processSubmit = async (imageData) => {
                const selectedAmenities = [];
                document.querySelectorAll('input[name="amenities"]:checked').forEach(cb => {
                    selectedAmenities.push(parseInt(cb.value, 10));
                });

                const pgData = {
                    ownerId: parseInt(loggedInUserId, 10),
                    pgName: document.getElementById('pg-name').value,
                    city: document.getElementById('pg-location').value,
                    address: document.getElementById('pg-address').value,
                    pgType: document.getElementById('owner-pg-type').value,
                    contactPhone: document.getElementById('pg-contact-phone').value,
                    contactEmail: document.getElementById('pg-contact-email').value,
                    roomType: document.getElementById('owner-room-type').value,
                    rent: parseInt(document.getElementById('pg-rent').value, 10),
                    description: document.getElementById('pg-description').value,
                    rules: document.getElementById('pg-rules').value,
                    amenities: selectedAmenities,
                    image: imageData
                };

                const url = editId ? `${API_URL}/pgs/${editId}` : `${API_URL}/pgs`;
                const method = editId ? 'PUT' : 'POST';

                try {
                    const response = await fetch(url, {
                        method: method,
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(pgData)
                    });
                    if (response.ok) {
                        alert(`PG ${editId ? 'Updated' : 'Added'} Successfully!`);
                        fetchAndRenderOwnerPgs();
                        resetForm();
                        showView('view-listings');
                    } else { throw new Error('Server responded with an error.'); }
                } catch (error) { alert('An error occurred. Please try again.'); }
            };

            if (imageFile) {
                const reader = new FileReader();
                reader.onloadend = () => processSubmit(reader.result);
                reader.readAsDataURL(imageFile);
            } else if (editId) {
                 fetch(`${API_URL}/pgs/${editId}`).then(res => res.json()).then(existingPg => {
                    processSubmit(existingPg ? existingPg.image : null);
                });
            } else {
                alert("Please select an image for the new PG.");
            }
        });

        myPgListingsDiv.addEventListener('click', async (e) => {
            const editButton = e.target.closest('.btn-edit');
            if (editButton) { populateFormForEdit(parseInt(editButton.dataset.id, 10)); }
            
            const deleteButton = e.target.closest('.btn-delete');
            if (deleteButton) {
                if (confirm('Are you sure you want to delete this PG?')) {
                    const pgId = parseInt(deleteButton.dataset.id, 10);
                    await fetch(`${API_URL}/pgs/${pgId}`, { method: 'DELETE' });
                    fetchAndRenderOwnerPgs();
                }
            }
        });

        bookingsTableBody.addEventListener('click', async (e) => {
            const target = e.target;
            const bookingId = target.dataset.id;
            
            if (target.classList.contains('btn-action-approve')) {
                const pgId = target.dataset.pgId;
                openRoomApprovalModal(bookingId, pgId);
            }
            
            else if (target.classList.contains('btn-action-reject')) {
                if (confirm(`Are you sure you want to reject this request?`)) {
                    try {
                        const response = await fetch(`${API_URL}/bookings/${bookingId}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ status: "Rejected" }) // Only send status
                        });
                        if (response.ok) {
                            alert(`Booking Rejected!`);
                            fetchBookingRequests(); 
                            fetchDashboardStats(); 
                        } else {
                            throw new Error(await response.text());
                        }
                    } catch (error) {
                        alert(error.message);
                    }
                }
            }
        });
        
        manageRentTableBody.addEventListener('click', async (e) => {
            const target = e.target;
            
            if (target.classList.contains('btn-action-paid')) {
                const tenancyId = target.dataset.tenancyId;
                const amount = target.dataset.amount;
                const currentMonth = new Date().toISOString().slice(0, 7); // "YYYY-MM"
                
                const month = prompt("Enter payment month (YYYY-MM):", currentMonth);
                if (!month) return; 
                
                const paymentData = {
                    tenancyId: parseInt(tenancyId, 10),
                    amount: parseFloat(amount),
                    paymentForMonth: month,
                    status: "Success"
                };

                try {
                    const response = await fetch(`${API_URL}/payments/record`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(paymentData)
                    });
                    if (response.ok) {
                        alert('Payment recorded successfully!');
                    } else {
                        throw new Error(await response.text());
                    }
                } catch (error) {
                    alert(`Error recording payment: ${error.message}`);
                }
            }

            if (target.classList.contains('btn-action-remind')) {
                const tenantId = target.dataset.tenantId;
                const pgId = target.dataset.pgId;
                const message = "This is a friendly reminder that your rent is due soon.";
                
                const notificationData = {
                    ownerId: parseInt(loggedInUserId, 10),
                    tenantId: parseInt(tenantId, 10),
                    pgId: parseInt(pgId, 10),
                    message: message
                };

                try {
                    const response = await fetch(`${API_URL}/notifications`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(notificationData)
                    });
                    if (response.ok) {
                        alert('Rent reminder sent!');
                        showView('view-notifications'); 
                    } else {
                        throw new Error(await response.text());
                    }
                } catch (error) {
                    alert(`Error sending reminder: ${error.message}`);
                }
            }
        });
        
        formCancelBtn.addEventListener('click', () => { resetForm(); showView('view-listings'); });

        pgSelectForRooms.addEventListener('change', () => {
            fetchRoomsForPg(pgSelectForRooms.value);
        });
        addRoomForm.addEventListener('submit', handleCreateRoom);

        async function openRoomApprovalModal(bookingId, pgId) {
            modalBookingIdInput.value = bookingId;
            modalRoomSelect.innerHTML = '<option value="">-- Loading available rooms... --</option>';
            roomApprovalModal.style.display = 'flex';
            
            const response = await fetch(`${API_URL}/rooms/pg/${pgId}/available`);
            if (response.status === 204) {
                modalRoomSelect.innerHTML = '<option value="">-- No available rooms found --</option>';
                return;
            }
            if (!response.ok) {
                modalRoomSelect.innerHTML = '<option value="">-- Error loading rooms --</option>';
                return;
            }
            
            const rooms = await response.json();
            modalRoomSelect.innerHTML = '<option value="">-- Select an available room --</option>';
            rooms.forEach(room => {
                const option = document.createElement('option');
                option.value = room.roomId;
                option.textContent = `${room.roomNumber} (₹${room.rent})`;
                modalRoomSelect.appendChild(option);
            });
        }
        
        function closeRoomApprovalModal() {
            roomApprovalModal.style.display = 'none';
            modalBookingIdInput.value = '';
            modalRoomSelect.innerHTML = '';
        }
        
        async function handleConfirmApproval() {
            const bookingId = modalBookingIdInput.value;
            const roomId = modalRoomSelect.value;
            
            if (!roomId) {
                alert("Please select a room to assign.");
                return;
            }
            
            try {
                const response = await fetch(`${API_URL}/bookings/${bookingId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ 
                        status: "Approved",
                        roomId: parseInt(roomId, 10) 
                    })
                });
                
                if (response.ok) {
                    alert('Booking Approved and Tenancy Created!');
                    closeRoomApprovalModal();
                    fetchBookingRequests(); 
                    fetchDashboardStats(); 
                } else {
                    throw new Error(await response.text());
                }
            } catch (error) {
                alert(`Error approving booking: ${error.message}`);
            }
        }
        
        modalConfirmApproveBtn.addEventListener('click', handleConfirmApproval);
        modalCancelApproveBtn.addEventListener('click', closeRoomApprovalModal);
        modalCloseApproveBtn.addEventListener('click', closeRoomApprovalModal);

        fetchDashboardStats(); 
    }
});