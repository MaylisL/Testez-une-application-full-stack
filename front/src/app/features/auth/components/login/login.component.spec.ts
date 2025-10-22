import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let mockAuthService: Partial<AuthService>;
  let mockSessionService: Partial<SessionService>;
  let mockRouter: Partial<Router>;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    mockAuthService = {
      login: jest.fn().mockReturnValue(of({
        token: 'fake-token',
        type: 'USER',
        username: 'testUserName',
        firstName: 'TestFirstName',
        lastName: 'TestLastName',
        id: 1,
        admin: false
      } as SessionInformation))
    };

    mockSessionService = {
      logIn: jest.fn(),
    };

    mockRouter = {
      navigate: jest.fn()
    };

    formBuilder = new FormBuilder();

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: FormBuilder, useValue: formBuilder }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call authService.login on submit ', () => {
    component.submit();
    expect(mockAuthService.login).toHaveBeenCalledTimes(1);
  });

  it('should call sessionService.logIn and navigate on authService.login success', () => {
    component.submit();
    expect(mockSessionService.logIn).toHaveBeenCalledTimes(1);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set onError to true and show error message when login fails', () => {
    (mockAuthService.login as jest.Mock).mockReturnValueOnce(
      throwError(() => new Error('Invalid credentials'))
    );

    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('wrongpass');
    component.submit();
    fixture.detectChanges();

    expect(component.onError).toBe(true);

    const errorElement = fixture.nativeElement.querySelector('.error');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('An error occurred');
  });

});
