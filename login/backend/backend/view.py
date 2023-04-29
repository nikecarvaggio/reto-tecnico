
from backend.serializer import UserSerializer
from django.contrib.auth.models import User
from django.shortcuts import get_object_or_404
from rest_framework import viewsets
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes

@permission_classes((IsAuthenticated, ))
class UserViewSet(viewsets.ViewSet):
    """
    A simple ViewSet for listing or retrieving users.
    """
    
    def list(self, request):
        uid = request.user.id
        queryset = User.objects.filter(pk=uid)
      
        serializer_context = {
            'request': request,
        }
        serializer = UserSerializer(queryset, context=serializer_context, many=True)
        return Response(serializer.data)
