// calendar.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {
  currentDate = new Date();
  currentMonth: string = '';
  currentYear: number = 0;
  calendarDays: Date[] = [];
  weekDays: string[] = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  ngOnInit() {
    this.updateCalendar();
  }

  updateCalendar() {
    const date = new Date(this.currentDate);
    this.currentMonth = date.toLocaleString('default', { month: 'long' });
    this.currentYear = date.getFullYear();
    this.generateCalendarDays(date);
  }

  generateCalendarDays(date: Date) {
    // Calendar generation logic
    this.calendarDays = [];
    const firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
    const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
    
    // Add days to calendar array
    for (let d = firstDay; d <= lastDay; d.setDate(d.getDate() + 1)) {
      this.calendarDays.push(new Date(d));
    }
  }

  previousMonth() {
    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
    this.updateCalendar();
  }

  nextMonth() {
    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
    this.updateCalendar();
  }

  isToday(date: Date): boolean {
    const today = new Date();
    return date.toDateString() === today.toDateString();
  }

  hasTasksForDay(date: Date): boolean {
    // Implement logic to check if there are tasks for this date
    return false;
  }
}
