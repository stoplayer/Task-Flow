import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideToastr } from 'ngx-toastr'; // Import provideToastr
import { importProvidersFrom } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; // Import BrowserAnimationsModule

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideToastr({ // Add Toastr configuration
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true
    }),
    importProvidersFrom(BrowserAnimationsModule) // Import BrowserAnimationsModule
  ]
};