console.log("Script loaded");

let currentTheme = getTheme();

// Initialize on DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
  changeTheme();
});

// Change theme functionality
function changeTheme() {
  changePageTheme(currentTheme, "");

  // Set the listener for the theme toggle button
  const changeThemeButton = document.querySelector("#changeThemeButton");
  if (changeThemeButton) {
    changeThemeButton.addEventListener("click", (event) => {
      let oldTheme = currentTheme;
      console.log("Change theme button clicked");

      // Toggle between light and dark
      if (currentTheme === "dark") {
        currentTheme = "light";
      } else {
        currentTheme = "dark";
      }
      console.log(currentTheme);
      changePageTheme(currentTheme, oldTheme);
    });
  } else {
    console.error("#changeThemeButton not found");
  }
}

// Set theme to localStorage
function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

// Get theme from localStorage
function getTheme() {
  let theme = localStorage.getItem("theme");
  return theme ? theme : "light";  // Default to light theme if not found
}

// Change the theme on the current page
function changePageTheme(theme, oldTheme) {
  // Update localStorage with the new theme
  setTheme(theme);

  // Remove the old theme class
  if (oldTheme) {
    document.querySelector("html").classList.remove(oldTheme);
  }

  // Apply the new theme class
  document.querySelector("html").classList.add(theme);

  // Change the button text to reflect the current theme
  const changeThemeButton = document.querySelector("#changeThemeButton");
  if (changeThemeButton) {
    const span = changeThemeButton.querySelector("span");
    if (span) {
      span.textContent = theme === "light" ? "Dark" : "Light";
    } else {
      console.error("Span element not found inside #changeThemeButton");
    }
  } else {
    console.error("#changeThemeButton not found");
  }
}
