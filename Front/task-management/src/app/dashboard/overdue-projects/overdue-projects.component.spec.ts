import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverdueProjectsComponent } from './overdue-projects.component';

describe('OverdueTasksComponent', () => {
  let component: OverdueProjectsComponent;
  let fixture: ComponentFixture<OverdueProjectsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverdueProjectsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverdueProjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
