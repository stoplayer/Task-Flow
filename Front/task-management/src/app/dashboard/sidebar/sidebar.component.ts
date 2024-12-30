// sidebar/sidebar.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  constructor(private router: Router) {}
  isCollapsed = false;
  userName: string = 'John Doe'; 
  userImg: string = '';

  ngOnInit() {
    // Retrieve the user's name directly from localStorage
    const storedName = localStorage.getItem('name'); // Retrieve "name" key
    if (storedName) {
      this.userName = storedName; // Update the userName
    }

    // Retrieve the user's image from localStorage
    const storedImg = localStorage.getItem('userImg'); // Retrieve "userImg" key
    if (storedImg) {
      this.userImg = `data:image/png;base64,${storedImg}`; // Update the userImg as Base64
    }
  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }

  signOut() {
    localStorage.removeItem('token'); // Remove the token from localStorage
    localStorage.removeItem('name');
    localStorage.removeItem('userId');
    localStorage.removeItem('userImg'); // Remove the user image from localStorage
    console.log('User signed out and token removed from localStorage.');
    this.router.navigate(['/sign-in']); // Redirect the user to the sign-in page
  }
}
