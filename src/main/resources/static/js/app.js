// Global Application Javascript & Theme Manager (Standard Spring Boot Page Navigation)

(function () {
    // 1. Theme Initialization (Run immediately to prevent theme flicker on page load)
    const savedTheme = localStorage.getItem('theme');
    const systemPrefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    const initialTheme = savedTheme ? savedTheme : (systemPrefersDark ? 'dark' : 'light');
    document.documentElement.setAttribute('data-theme', initialTheme);
})();

document.addEventListener('DOMContentLoaded', () => {
    // 2. Initialize Theme Switcher Buttons
    updateThemeToggleUI();

    const toggleBtns = document.querySelectorAll('.theme-toggle-btn');
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeToggleUI();
        });
    });

    // 3. Preserve Sidebar Scroll Position & Auto-Scroll Active Link into View
    const sidebarNav = document.querySelector('.sidebar-nav');
    if (sidebarNav) {
        // Save scroll position when clicking any sidebar link
        const navLinks = sidebarNav.querySelectorAll('a');
        navLinks.forEach(link => {
            link.addEventListener('click', () => {
                sessionStorage.setItem('sidebarScrollTop', sidebarNav.scrollTop);
            });
        });

        // Restore saved scroll position or auto-scroll active item into view
        const savedScrollTop = sessionStorage.getItem('sidebarScrollTop');
        const activeLink = sidebarNav.querySelector('a.active');

        if (savedScrollTop !== null) {
            sidebarNav.scrollTop = parseInt(savedScrollTop, 10);
        } else if (activeLink) {
            activeLink.scrollIntoView({ block: 'nearest', inline: 'nearest' });
        }

        // Ensure active item is always visible inside viewport if it's near bottom/top
        if (activeLink) {
            const navRect = sidebarNav.getBoundingClientRect();
            const linkRect = activeLink.getBoundingClientRect();
            if (linkRect.bottom > navRect.bottom || linkRect.top < navRect.top) {
                activeLink.scrollIntoView({ block: 'nearest', inline: 'nearest' });
            }
        }
    }

    // 4. Auto-dismiss Alert Banners after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            try {
                if (window.bootstrap && bootstrap.Alert) {
                    const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                    bsAlert.close();
                }
            } catch (e) {
                // Ignore if alert already closed manually
            }
        }, 5000);
    });

    // 5. Update unread notification count badge periodically
    const unreadBadge = document.getElementById('unreadNotificationBadge');
    if (unreadBadge) {
        fetch('/api/student/unread-notifications-count')
            .then(res => res.json())
            .then(data => {
                if (data && data.count > 0) {
                    unreadBadge.textContent = data.count;
                    unreadBadge.classList.remove('d-none');
                } else {
                    unreadBadge.classList.add('d-none');
                }
            })
            .catch(err => console.log('Notification fetch error:', err));
    }
});

// Helper function to update Sun/Moon icon & label in all Theme Toggle buttons
function updateThemeToggleUI() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    const isDark = currentTheme === 'dark';
    
    const toggleBtns = document.querySelectorAll('.theme-toggle-btn');
    toggleBtns.forEach(btn => {
        const icon = btn.querySelector('i');
        const label = btn.querySelector('.theme-label');
        
        if (icon) {
            icon.className = isDark ? 'bi bi-sun-fill text-warning' : 'bi bi-moon-stars-fill text-primary';
        }
        if (label) {
            label.textContent = isDark ? 'Light Mode' : 'Dark Mode';
        }
    });
}
