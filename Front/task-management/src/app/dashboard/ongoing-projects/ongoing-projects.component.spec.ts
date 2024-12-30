import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OngoingTasksComponent } from './ongoing-tasks.component';

describe('OngoingTasksComponent', () => {
  let component: OngoingTasksComponent;
  let fixture: ComponentFixture<OngoingTasksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OngoingTasksComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OngoingTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
