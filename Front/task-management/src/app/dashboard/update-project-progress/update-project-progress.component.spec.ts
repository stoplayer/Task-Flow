import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateProjectProgressComponent } from './update-project-progress.component';

describe('UpdateProjectProgressComponent', () => {
  let component: UpdateProjectProgressComponent;
  let fixture: ComponentFixture<UpdateProjectProgressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateProjectProgressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateProjectProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
