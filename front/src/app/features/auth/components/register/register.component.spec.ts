import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  let mockAuthService: Partial<AuthService>;
  let mockRouter: Partial<Router>;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    formBuilder = new FormBuilder();
    mockAuthService = {
      register: jest.fn().mockReturnValue(of(void 0))
    };
    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: FormBuilder, useValue: formBuilder }
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have submit button disabled when form is invalid', () => {
      component.form.setValue({
        email: '',
        firstName: '',
        lastName: '',
        password: ''
      });
      fixture.detectChanges();

      const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
      expect(submitButton.disabled).toBe(true);
    });


  it('should call and navigate on success', () => {
    component.submit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

   it('should set onError to true on authService.register failure', () => {
      (mockAuthService.register as jest.Mock).mockReturnValueOnce(
        throwError(() => new Error('Unable to register'))
      );
      component.submit();
      fixture.detectChanges();

      expect(component.onError).toBe(true);
      const errorEl = fixture.nativeElement.querySelector('.error');
      expect(errorEl).toBeTruthy();
      expect(errorEl.textContent).toContain('An error occurred');
      expect(mockRouter.navigate).not.toHaveBeenCalled();
   });
});

